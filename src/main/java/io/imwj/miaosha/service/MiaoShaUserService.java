package io.imwj.miaosha.service;

import io.imwj.miaosha.dao.MiaoShaUserMapper;
import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.exception.GlobalException;
import io.imwj.miaosha.redis.MiaoShaUserKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.util.MD5Util;
import io.imwj.miaosha.util.UUIDUtil;
import io.imwj.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆Service
 * @author langao_q
 * @since 2020-11-24 16:51
 */
@Service
public class MiaoShaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoShaUserMapper userMapper;

    /**
     * 用户登陆方法
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        //得到数据库中的用户信息
        MiaoShaUser dbUser = userMapper.getUserById(loginVo.getMobile());

        //验证用户是否存在
        if(dbUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //验证密码
        String dbPass = dbUser.getPassword();
        String inputPass = MD5Util.formPassToDbPass(loginVo.getPassword(), dbUser.getSalt());
        if(!inputPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //登陆成功后生成token并存放到redis中 - 同时存放到cookie中
        String token = UUIDUtil.uuid();
        redisService.set(MiaoShaUserKey.TOKEN, token, dbUser);
        addCookie(response, COOKI_NAME_TOKEN, token);
        return token;
    }

    /**
     * 根据token获取user用户，如果user不为空就要延迟redis和cookie中的有效期
     * @param response
     * @param token
     * @return
     */
    public MiaoShaUser getByToken(HttpServletResponse response, String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoShaUser user = redisService.get(MiaoShaUserKey.TOKEN, token, MiaoShaUser.class);
        if(user != null){
            //延长cookie中token的有效期
            addCookie(response, COOKI_NAME_TOKEN, token);
            //延长redis中token的有效期
            redisService.set(MiaoShaUserKey.TOKEN, token, user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(MiaoShaUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
