package io.imwj.miaosha.config;

import io.imwj.miaosha.access.AccessInterceptor;
import io.imwj.miaosha.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author langao_q
 * @since 2020-11-25 18:22
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    /**
     * 校验Cookie或Request中token，为空或者token没有对应用户信息的就去登陆页面
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登陆拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/bootstrap/**", "/img/**", "/jquery-validation/**", "/js/**", "/layer/**");
        //限流拦截器
        registry.addInterceptor(accessInterceptor);
    }

    /**
     * 根据Cookie或Request中token直接获取user对象注入的Controller中
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    /**
     * 对静态资源放行
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }


}