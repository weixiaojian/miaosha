package io.imwj.miaosha.redis;

/**
 * User - Prefix的标识
 * @author langao_q
 * @since 2020-11-19 14:30
 */
public class OrderKey extends BasePrefix {


    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("order-record");

}
