package io.imwj.miaosha.service;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.OrderInfo;
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


    /**
     * 秒杀：减库存 下订单 写入秒杀订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
        //1.减少库存
        goodsService.reduceStock(goods);

        //2.写入订单
        OrderInfo info = orderService.createOrder(user, goods);
        return info;
    }
}
