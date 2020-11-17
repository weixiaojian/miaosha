package io.imwj.miaosha.result;

import lombok.Data;

/**
 * 结果返回类
 *
 * @author langao_q
 * @since 2020-11-17 17:02
 */
@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public Result(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
    }

    public Result(CodeMsg cm) {
        if (cm == null) {
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    /**
     * 成功调用
     *
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败调用
     *
     * @return
     */
    public static <T> Result error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
}
