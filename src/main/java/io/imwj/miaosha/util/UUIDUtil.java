package io.imwj.miaosha.util;

import java.util.UUID;

/**
 * uuid生成
 * @author langao_q
 * @since 2020-11-25 17:58
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
