package com.xjh.myblog.common.utils;

import com.xjh.myblog.common.api.ResultCode;
import com.xjh.myblog.common.exception.MyException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;

public class AopUtil {
    // 获取方法的某一个参数,从0开始算
    public static <T> T getArgs(JoinPoint joinPoint, int n, Class<T> tClass){
        try {
            return tClass.cast(joinPoint.getArgs()[n]);
        } catch (Exception e){
            return null;
        }
    }
    // 获取某一个方法上的注解
    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint,Class<T> tClass){
        try {
            return ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(tClass);
        } catch (Exception e){
            return null;
        }

    }
}
