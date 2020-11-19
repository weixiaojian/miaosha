package io.imwj.miaosha.redis;

/**
 * @author langao_q
 * @since 2020-11-19 14:26
 */
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
