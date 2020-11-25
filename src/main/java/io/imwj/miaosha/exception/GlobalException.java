package io.imwj.miaosha.exception;

import io.imwj.miaosha.result.CodeMsg;

/**
 * 自定义异常
 * @author langao_q
 * @since 2020-11-24 17:55
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
