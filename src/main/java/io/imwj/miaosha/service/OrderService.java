package io.imwj.miaosha.service;

import io.imwj.miaosha.dao.OrderMapper;
import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import io.imwj.miaosha.redis.OrderKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 订单Service
 * @author langao_q
 * @since 2020-11-30 11:30
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisService redisService;

    /**
     * 根据用户id和商品id获取秒杀订单
     * 优化后：把秒杀的历史记录存入redis(key = userId+"_"+goodsId)
     * @param userId
     * @param goodsId
     * @return
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, String goodsId) {
        //return orderMapper.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, userId+"_"+goodsId, MiaoshaOrder.class);
    }

    /**
     * 创建订单：1.写入订单表  2.写入订单详情表
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods) {
        //1.写入订单详情表
        OrderInfo info = new OrderInfo();
        info.setCreateDate(new Date());
        info.setDeliveryAddrId(0L);
        info.setGoodsCount(1);
        info.setGoodsId(goods.getId());
        info.setGoodsName(goods.getGoodsName());
        info.setGoodsPrice(goods.getMiaoshaPrice());
        info.setOrderChannel(1);
        info.setStatus(0);
        info.setUserId(user.getId());
        orderMapper.insert(info);


        //2.写入订单主表
        MiaoshaOrder order = new MiaoshaOrder();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setOrderId(info.getId());
        orderMapper.insertMiaoshaOrder(order);

        //3.把秒杀记录写入redis
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, user.getId()+"_"+info.getId(), order);
        return info;
    }

    /**
     * 根据id查询订单
     * @param orderId
     * @return
     */
    public OrderInfo getOrderById(long orderId){
        return orderMapper.getOrderById(orderId);
    }
}
