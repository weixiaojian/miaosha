package io.imwj.miaosha.redis;

/**
 * 秒杀redis使用KEY
 * @author langao_q
 * @since 2021-01-12 18:19
 */
public class MiaoshaKey extends BasePrefix{



    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 标识商品是否秒杀完
     */
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"isGoodsOver");

    /**
     * 储存秒杀路径PathVariable参数
     */
    public static KeyPrefix getMiaoshaPath = new MiaoshaKey(60,"MiaoshaPath");

    /**
     * 储存图形验证码
     */
    public static KeyPrefix getMiaoshaVerifyCode = new MiaoshaKey(300,"MiaoshaVerifyCode");
}
