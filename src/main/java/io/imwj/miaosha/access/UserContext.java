package io.imwj.miaosha.access;

import io.imwj.miaosha.domain.MiaoShaUser;

/**
 * 使用ThreadLocal在多线程下 储存User
 * @author langao_q
 * @since 2021-01-21 17:20
 */
public class UserContext {

    /**
     * 多线程下ThreadLocal是把数据绑定到每一个线程中去的，是线程安全的
     */
    private static ThreadLocal<MiaoShaUser> threadLocal = new ThreadLocal();

    /**
     * 将User存入ThreadLocal
     * @param user
     */
    public static void set(MiaoShaUser user){
        threadLocal.set(user);
    }

    /**
     * 从ThreadLocal获取User
     * @return
     */
    public static MiaoShaUser get(){
        return threadLocal.get();
    }

}
