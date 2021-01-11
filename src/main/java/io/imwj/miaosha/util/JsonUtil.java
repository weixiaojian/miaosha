package io.imwj.miaosha.util;

import com.alibaba.fastjson.JSON;
import org.thymeleaf.util.StringUtils;

/**
 * json工具类
 * @author langao_q
 * @since 2021-01-11 17:39
 */
public class JsonUtil {

    /**
     * 将Bean对象转换成String
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        } else if (value == Integer.class) {
            return "" + value;
        } else if (value == String.class) {
            return (String) value;
        } else if (value == Long.class) {
            return "" + value;
        }else {
            return JSON.toJSONString(value);
        }
    }


    /**
     * 将字符串转换Bean对象
     *
     * @param str
     * @param <T>
     * @return
     */
    public static  <T> T stringToBean(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str)) {
            return null;
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(str);
        }else {
            return JSON.parseObject(str, clazz);
        }
    }
}
