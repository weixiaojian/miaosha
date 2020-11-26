package io.imwj.miaosha.vo;

import io.imwj.miaosha.domain.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商品返回Vo
 * @author langao_q
 * @since 2020-11-26 18:08
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class GoodsVo extends Goods {

    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
