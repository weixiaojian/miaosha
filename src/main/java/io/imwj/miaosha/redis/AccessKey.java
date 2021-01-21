package io.imwj.miaosha.redis;

/**
 * 限流key
 * @author langao_q
 * @since 2021-01-21 17:46
 */
public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


    public static AccessKey withExpire(int seconds) {
        return new AccessKey(seconds, "accessKey");
    }
}
