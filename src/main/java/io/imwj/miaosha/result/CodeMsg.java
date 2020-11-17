package io.imwj.miaosha.result;

import lombok.Data;

/**
 * 错误码对象
 * @author langao_q
 * @since 2020-11-17 17:06
 */
@Data
public class CodeMsg {

    private Integer code;
    private String msg;

    /**
     * 成功标识
     */
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    /**
     * 失败标识
     */
    public static CodeMsg ERROR = new CodeMsg(500, "服务端异常！");


    private CodeMsg(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

}
