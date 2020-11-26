package io.imwj.miaosha.domain;

import lombok.Data;

/**
 * 秒杀订单实体类
 * @author langao_q
 * @since 2020-11-26 18:07
 */
@Data
public class MiaoshaOrder {

    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;
}
