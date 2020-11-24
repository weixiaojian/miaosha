package io.imwj.miaosha.exception;

import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 * @author langao_q
 * @since 2020-11-24 17:51
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

     @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        //自定义异常处理
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }
        //Valid参数校验异常处理
        else if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        //其他异常处理
        else{
            log.error("【出现异常】：", e);
            return Result.error(CodeMsg.ERROR);
        }
     }

}
