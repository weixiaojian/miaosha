package io.imwj.miaosha.redis;

/**
 * User - Prefix的标识
 * @author langao_q
 * @since 2020-11-19 14:30
 */
public class GoodsKey extends BasePrefix {


    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"goodsList");

    public static GoodsKey getGoodsById = new GoodsKey(60,"goodsById");

    public static GoodsKey getMiaoshaGoodsStock= new GoodsKey(0, "goodsStock");

}
