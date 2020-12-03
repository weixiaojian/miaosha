package io.imwj.miaosha.vo;

import io.imwj.miaosha.domain.OrderInfo;
import lombok.Data;

/**
 * @author langao_q
 * @since 2020-12-03 17:51
 */
@Data
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
}
