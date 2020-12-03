package io.imwj.miaosha.redis;

/**
 * User - Prefix的标识
 * @author langao_q
 * @since 2020-11-19 14:30
 */
public class MiaoShaUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600*24 * 7;

    public MiaoShaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoShaUserKey TOKEN = new MiaoShaUserKey(TOKEN_EXPIRE,"token");

    public static MiaoShaUserKey getById = new MiaoShaUserKey(0, "id");

}
