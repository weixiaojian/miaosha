package io.imwj.miaosha.access;

import com.alibaba.fastjson.JSON;
import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.exception.GlobalException;
import io.imwj.miaosha.redis.AccessKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 实现AccessLimit注解的限流功能
 * @author langao_q
 * @since 2021-01-21 17:34
 */
@Slf4j
@Component
public class AccessInterceptor  implements HandlerInterceptor {

    @Autowired
    private MiaoShaUserService userService;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {

        try {
            //拦截方法
            if(handler instanceof HandlerMethod) {
                //1.获取用户信息
                MiaoShaUser user = getUser(req, res);
                //2.存放到ThreaLocal中
                UserContext.set(user);
                //3.获取AccessLimit注解
                HandlerMethod hm = (HandlerMethod)handler;
                AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
                //没有加AccessLimit注解就放行
                if(accessLimit == null){
                    return true;
                }
                //4.确保用户已经登陆
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                boolean needLogin = accessLimit.needLogin();
                String key = req.getRequestURI();
                if(needLogin){
                    if(user == null) {
                        render(res, CodeMsg.SESSION_ERROR);
                        return false;
                    }
                    key += "_" + user.getId();
                }
                //5.接口限流
                AccessKey accessKey = AccessKey.withExpire(seconds);
                Integer count = redisService.get(accessKey, key, Integer.class);
                if(count  == null) {
                    redisService.set(accessKey, key, 1);
                }else if(count < maxCount) {
                    redisService.incr(accessKey, key);
                }else {
                    render(res, CodeMsg.ACCESS_LIMIT_REACHED);
                    return false;
                }
            }
            //放行
            return true;
        } catch (Exception e) {
            throw new GlobalException(CodeMsg.SYS_ERROR.systemErro(e));
        }
    }

    /**
     * 向客户端返回json数据
     * @param res
     * @param cm
     * @throws IOException
     */
    private void render(HttpServletResponse res, CodeMsg cm) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        OutputStream out = res.getOutputStream();
        String str  = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 根据token获取User信息
     * @param req
     * @param res
     */
    private MiaoShaUser getUser(HttpServletRequest req, HttpServletResponse res) {

        String tokenParam = req.getParameter(MiaoShaUserService.COOKI_NAME_TOKEN);
        String tokenCookie = getTokenCookie(req);
        String token = StringUtils.isNotEmpty(tokenParam)?tokenParam:tokenCookie;
        //tokenParam为空时返回空
        if(StringUtils.isEmpty(tokenParam) && StringUtils.isEmpty(tokenCookie)) {
            return null;
        }
        return userService.getByToken(res, token);
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
