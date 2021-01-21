package io.imwj.miaosha.access;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义限流注解
 *
 * @Retention(RUNTIME) //运行时启用
 * @Target(METHOD) //标注在方法上
 * @author langao_q
 * @since 2021-01-21 17:17
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
    /**
     * 时间 s
     * @return
     */
    int seconds();

    /**
     * 最大次数
     * @return
     */
    int maxCount();

    /**
     * 是否需要登陆 true
     * @return
     */
    boolean needLogin() default true;
}
