package io.imwj.miaosha.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author langao_q
 * @since 2020-11-24 17:48
 */
@Slf4j
@Aspect
@Configuration
public class LogRecordAspect {

    /**
     * 定义切点Pointcut
     */
    @Pointcut("execution(* io.imwj.miaosha.controller.*Controller*.*(..))")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String paraString = JSON.toJSONString(request.getParameterMap());
        log.info("【请求开始】URI: {}, method: {}, params: {}", uri, method, paraString);

        long begin = System.nanoTime();
        Object result = pjp.proceed();
        long end = System.nanoTime();

        log.info("【请求结束】controller的返回值是 " + JSON.toJSONString(result));
        return result;
    }
}