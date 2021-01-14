package io.imwj.miaosha.service;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import io.imwj.miaosha.redis.MiaoshaKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
