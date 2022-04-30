package com.xjh.myblog.app.aspect;

import com.xjh.myblog.app.annotation.CacheException;
import com.xjh.myblog.common.api.ResultCode;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.common.utils.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class RedisCacheException {

    @Pointcut(value = "execution(public * com.xjh.myblog.app.cacheservice.*CacheService.*(..))")
    public void cacheAspect(){}

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable){
            // 有CacheException注解的方法需要抛出异常
            if(AopUtil.getAnnotation(point, CacheException.class) != null){
                throw new MyException(ResultCode.REDIS_ERROR.getCode(),ResultCode.REDIS_ERROR.getMessage() + ":" + throwable.getMessage());
            }else{
                log.error("redis出错:" + throwable.getMessage());
            }
        }
        return result;
    }

}
