package io.imwj.miaosha.vo;

import io.imwj.miaosha.domain.MiaoShaUser;
import lombok.Data;

/**
 * @author langao_q
 * @since 2020-12-03 17:11
 */
@Data
public class GoodsDetailVo {

    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private MiaoShaUser user;


}
