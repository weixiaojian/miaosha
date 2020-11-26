package io.imwj.miaosha.domain;

import lombok.Data;

/**
 * 商品实体类
 * @author langao_q
 * @since 2020-11-26 18:06
 */
@Data
public class Goods {

    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;
}
