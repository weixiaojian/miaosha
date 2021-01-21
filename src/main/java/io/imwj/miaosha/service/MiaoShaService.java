package io.imwj.miaosha.service;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import io.imwj.miaosha.redis.MiaoshaKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.util.MD5Util;
import io.imwj.miaosha.util.UUIDUtil;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * 秒杀Service
 * @author langao_q
 * @since 2020-11-30 11:23
 */
@Service
public class MiaoShaService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;


    /**
     * 秒杀：减库存 下订单 写入秒杀订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
        //1.减少库存
        boolean boo = goodsService.reduceStock(goods);
        if(boo){
            //2.写入订单
            OrderInfo info = orderService.createOrder(user, goods);
            return info;
        }else {
            //3.表示库存扣减失败 商品已秒杀完
            setGoodsOver(goods.getId());
        }
        return null;
    }

    /**
     * 获取秒杀结果（查询是否生成了订单）
     * 注意：获取秒杀结果时 可能商品已经扣减了库存但还没有生成订单，所以redis中存了一个值来标识是否某一商品是否秒杀完setGoodsOver
     * 生成了订单：返回成功 1
     * 未生成订单商品没有库存：返回失败 -1
     * 未生成订单商品还有库存：继续轮询 0
     * @param userId
     * @param goodsId
     * @return
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId + "");
        //秒杀成功
        if(order != null) {
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }

    /**
     * 1.重置mysql中的商品库存
     * 2。清楚订单记录
     * @param goodsList
     */
    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    /**
     * 校验验证码是否正确
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(MiaoShaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    /**
     * 生成秒杀请求url的PathVariable参数
     * @param user
     * @param goodsId
     * @return
     */
    public String createMiaoshaPath(MiaoShaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.MD5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getId() + "_"+ goodsId, str);
        return str;
    }

    /**
     * 验证秒杀请求中的PathVariable参数
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(MiaoShaUser user, String goodsId, String path) {
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    /**
     * 生成图片验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(MiaoShaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

}
