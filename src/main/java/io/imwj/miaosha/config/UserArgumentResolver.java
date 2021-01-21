package io.imwj.miaosha.config;

import io.imwj.miaosha.access.UserContext;
import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 把前端传过来的token转换成User  赋值到controller方法中User参数
 * @author langao_q
 * @since 2020-11-25 18:22
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoShaUserService userService;

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
        return  UserContext.get();
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
