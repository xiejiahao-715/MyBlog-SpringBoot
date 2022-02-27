package com.xjh.myblog.exceptionhandler;

import com.xjh.myblog.constant.IResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 全局异常处理
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public IResult globalError(Exception e){
        e.printStackTrace();
        return IResult.error().message(e.getMessage());
    }

    @ExceptionHandler(MyException.class)
    public IResult defineError(MyException e){
        e.printStackTrace();
        return IResult.error()
                .code(e.getCode())
                .message(e.getMessage())
                .data(e.getData());
    }
}
