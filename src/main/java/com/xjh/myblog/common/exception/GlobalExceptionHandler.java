package com.xjh.myblog.common.exception;

import com.xjh.myblog.common.api.ResultCode;
import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.common.utils.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

// 全局异常处理
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    // 处理全局的任何异常
    @ExceptionHandler(Exception.class)
    public CommonResult globalError(Exception e){
        log.error(e.getMessage(),e);
        return CommonResult.build().success(false).code(ResultCode.SYSTEM_ERROR.getCode()).message(e.getMessage());
    }
    // 对表单验证时抛出的 MethodArgumentNotValidException 异常做统一处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult validError(MethodArgumentNotValidException e){
        log.error(e.getMessage(),e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        for(ObjectError objectError : errors){
            errorMsg.append(objectError.getDefaultMessage());
            errorMsg.append(";");
        }
        // 删除末尾冗余的 ;
        if(errorMsg.length() >= 1){
            errorMsg.deleteCharAt(errorMsg.length() - 1);
        }
        return CommonResult.build().success(false).code(ResultCode.VALID_ERROR.getCode()).message(errorMsg.toString());
    }

    // 处理自定义的异常
    @ExceptionHandler(MyException.class)
    public CommonResult defineError(MyException e){
        log.error(e.getMessage());
        // 如果错误为黑名单IP，改变状态码
        if (ResultCode.LIMIT_IP_REQUEST.getCode().equals(e.getCode())) {
            HttpServletResponse response = ServletUtil.getHttpServletResponse();
            response.setStatus(e.getCode());
        }
        return CommonResult.build().success(false).code(e.getCode()).message(e.getMessage()).data(e.getData());
    }
}
