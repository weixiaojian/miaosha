package io.imwj.miaosha.redis;

/**
 * 秒杀redis使用KEY
 * @author langao_q
 * @since 2021-01-12 18:19
 */
public class MiaoshaKey extends BasePrefix{

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    /**
     * 标识商品是否秒杀完
     */
    public static MiaoshaKey isGoodsOver = new MiaoshaKey("isGoodsOver");
}
