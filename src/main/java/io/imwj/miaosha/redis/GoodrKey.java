package io.imwj.miaosha.redis;

/**
 * User - Prefix的标识
 * @author langao_q
 * @since 2020-11-19 14:30
 */
public class GoodrKey extends BasePrefix {


    public GoodrKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodrKey getGoodsList = new GoodrKey(60,"goodsList");

    public static GoodrKey getGoodsById = new GoodrKey(60,"goodsById");

}
