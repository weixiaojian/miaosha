package io.imwj.miaosha.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author langao_q
 * @since 2020-11-24 15:28
 */
@Slf4j
public class MD5Util {

    private static final String salt = "1a2b3c";


    /**
     * md5加密
     * @param str
     * @return
     */
    public static String MD5(String str){
        return DigestUtils.md5Hex(str);
    }

    /**
     * 输入的密码 + 固定salt加密
     * 截取salt指定位置的字符 + 输入的密码
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) +  salt.charAt(2) + inputPass + salt.charAt(5) +  salt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 获取到的表单的密码 + 随机salt加密
     * 截取salt指定位置的字符 + 输入的密码
     * @param formPass
     * @param saltForm
     * @return
     */
    public static String formPassToDbPass(String formPass, String saltForm){
        String str = "" + saltForm.charAt(0) +  saltForm.charAt(2) + formPass + saltForm.charAt(5) +  saltForm.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 输入的密码 + 随机的salt加密  同时salt保存到数据库中
     * @param inputPass
     * @param saltDb
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String saltDb){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDbPass(formPass, saltDb);
        return dbPass;
    }



    public static void main(String[] args) {
        String dbPass = inputPassToDbPass("123123", salt);
        log.info(dbPass);
    }
}
