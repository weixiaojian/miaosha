package io.imwj.miaosha.config;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.redis.MiaoShaUserKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.service.MiaoShaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author langao_q
 * @since 2020-11-25 18:22
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private RedisService redisService;

    /**
     * 先判断参数中的类型是不是MiaoShaUser 是的话再执行resolveArgument
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== MiaoShaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeRequest(HttpServletResponse.class);
        String tokenParam = request.getParameter("token");
        String tokenCookie = getTokenCookie(request);
        String token = StringUtils.isNotEmpty(tokenParam)?tokenParam:tokenCookie;

        if(StringUtils.isEmpty(tokenParam) && StringUtils.isEmpty(tokenCookie)) {
            return null;
        }
        MiaoShaUser user = redisService.get(MiaoShaUserKey.TOKEN, token, MiaoShaUser.class);
        return user;
    }

    private String getTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(MiaoShaUserService.COOKI_NAME_TOKEN)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
