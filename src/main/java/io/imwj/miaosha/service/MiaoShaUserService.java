package io.imwj.miaosha.service;

import io.imwj.miaosha.dao.MiaoShaMapper;
import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.exception.GlobalException;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.util.MD5Util;
import io.imwj.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 登陆Service
 * @author langao_q
 * @since 2020-11-24 16:51
 */
@Service
public class MiaoShaUserService {

    @Autowired
    private MiaoShaMapper userMapper;

    /**
     * 用户登陆方法
     * @param loginVo
     * @return
     */
    public boolean login(LoginVo loginVo) {
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
        return true;
    }
}
