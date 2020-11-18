package io.imwj.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author langao_q
 * @since 2020-11-18 16:24
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 根据Key获取数据
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            return stringToBean(str, clazz);
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            jedis.set(key, str);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将Bean对象转换成String
     * @param value
     * @param <T>
     * @return
     */
    private <T> String beanToString(T value) {
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
    private <T> T stringToBean(String str, Class<T> clazz) {
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

    /**
     * 回收jedis到池中
     *
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
