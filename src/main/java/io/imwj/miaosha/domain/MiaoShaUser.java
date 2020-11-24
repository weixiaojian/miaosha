package io.imwj.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * user实体类
 * @author langao_q
 * @since 2020-11-24 17:10
 */
@Data
public class MiaoShaUser {

    /**
     * 用户手机号
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码
     */
    private String password;
    /**
     * 盐
     */
    private String salt;
    /**
     * 头像id
     */
    private String head;
    /**
     * 注册时间
     */
    private Date registerDate;
    /**
     * 最后登陆时间
     */
    private Date lastLoginDate;
    /**
     * 登陆次数
     */
    private Integer loginCount;
}
