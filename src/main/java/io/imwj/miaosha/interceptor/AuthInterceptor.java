package io.imwj.miaosha.interceptor;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.redis.MiaoShaUserKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 校验Cookie或Request中token，为空或者token没有对应用户信息的就去登陆页面
 * @author langao_q
 * @since 2020-11-25 18:39
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {

        try {
            String tokenParam = req.getParameter(MiaoShaUserService.COOKI_NAME_TOKEN);
            String tokenCookie = getTokenCookie(req);

            String token = StringUtils.isNotEmpty(tokenParam)?tokenParam:tokenCookie;

            //为空时跳转到登陆页面
            if(StringUtils.isEmpty(tokenParam) && StringUtils.isEmpty(tokenCookie)) {
                res.sendRedirect("/login/toLogin");
                return false;
            }

            //redis中的用户数据为空时跳转到登陆页面
            MiaoShaUser user = redisService.get(MiaoShaUserKey.TOKEN, token, MiaoShaUser.class);
            if(user == null) {
                res.sendRedirect("/login/toLogin");
                return false;
            }

            //放行
            return true;
        } catch (Exception e) {
            log.error("-----授权错误-----" + e.getMessage());
            res.sendRedirect("/hello");
            return false;
        }
    }


    /**
     * 获取cookie中的token
     * @param request
     * @return
     */
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
