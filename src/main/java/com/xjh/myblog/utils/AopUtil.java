package com.xjh.myblog.utils;

import com.xjh.myblog.ENUM.IResultCode;
import com.xjh.myblog.exceptionhandler.MyException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;

public class AopUtil {
    // 获取方法的某一个参数,从0开始算
    public static <T> T getArgs(JoinPoint joinPoint, int n, Class<T> tClass){
        try {
            return tClass.cast(joinPoint.getArgs()[n]);
        } catch (Exception e){
            throw new MyException(IResultCode.SYSTEM_ERROR.getCode(), "服务器内部异常");
        }
    }
    // 获取某一个方法上的注解
    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint,Class<T> tClass){
        try {
            return ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(tClass);
        } catch (Exception e){
            throw new MyException(IResultCode.SYSTEM_ERROR.getCode(), "服务器内部异常");
        }

    }
}
