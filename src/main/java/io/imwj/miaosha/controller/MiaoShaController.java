package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import io.imwj.miaosha.redis.GoodsKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.GoodsService;
import io.imwj.miaosha.service.MiaoShaService;
import io.imwj.miaosha.service.OrderService;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * 秒杀控制类
 * @author langao_q
 * @since 2020-11-30 11:22
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoShaController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MiaoShaService miaoShaService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    /**
     * 标记秒杀已经结束
     */
    private HashMap<String, Boolean> localOverMap =  new HashMap<String, Boolean>();


    /**
     *  QPS：78.8
     *  5000个商品    5000线程/1次
     *
     *  QPS：60 - 600（商品库存秒杀完之后 qps开始上升）
     *  5000个商品    5000线程/10次
     * 商品秒杀
     * 1.判断用户是否登陆（拦截器AuthInterceptor已经处理）
     * 2.校验库存是否充足
     * 3.判断是否已经秒杀过了
     * 4.减库存 下订单 写入秒杀订单
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("do_miaosha")
    public String doMiaoSha(Model model,
                            MiaoShaUser user,
                            String goodsId){
        model.addAttribute("user", user);
        //1.判断用户是否登陆（拦截器AuthInterceptor已经处理）

        //2.校验库存是否充足
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //3.判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //4.减库存 下订单 写入秒杀订单
        OrderInfo info = miaoShaService.miaosha(user, goods);
        model.addAttribute("orderInfo", info);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    /**
     * 【秒杀优化】：
     * 1.系统初始化，把商品库存加载到redis中
     * 2.收到请求，redis预减库存 若库存不足直接返回
     * 3.请求入队rabbitMQ，立即返回排队中
     * 4.请求出队，生成订单 减少库存
     * 5.客户端轮询，是否秒杀成功
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @ResponseBody
    @RequestMapping("doMiaoSha2")
    public Result<Integer> doMiaoSha2(Model model,
                             MiaoShaUser user,
                             String goodsId){
        model.addAttribute("user", user);
        //1.判断用户是否登陆（拦截器AuthInterceptor已经处理）

        //2.校验库存是否充足（先使用内存标记 减少redis访问）
        Boolean put = localOverMap.get(goodsId + "");
        if(put){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        Long stockCount = redisService.dncr(GoodsKey.getMiaoshaGoodsStock, goodsId);
        if(stockCount <= 0){
            localOverMap.put(goodsId, false);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //3.判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //4.请求入队rabbitMQ（0：排队中）
        return Result.success(0);
    }

    /**
     * 系统初始化：
     * 把商品库存都加载到redis中去
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if(goodsVos == null){
            return ;
        }
        for (GoodsVo good : goodsVos){
            redisService.set(GoodsKey.getMiaoshaGoodsStock, good.getId() + "", good.getStockCount());
        }
    }

    /**
     * 秒杀结果轮询
     * 1 - 秒杀成功
     * 0 - 秒杀排队中
     * -1 - 秒杀失败
     * @param model
     * @return
     */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoShaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  = miaoShaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
}
