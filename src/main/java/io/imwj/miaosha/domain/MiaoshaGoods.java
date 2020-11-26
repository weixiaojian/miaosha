package io.imwj.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀商品类
 * @author langao_q
 * @since 2020-11-26 18:06
 */
@Data
public class MiaoshaGoods {

    private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
