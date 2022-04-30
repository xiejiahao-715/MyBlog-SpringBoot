package com.xjh.myblog.app.aspect;

import com.xjh.myblog.app.service.AccessLimitService;
import com.xjh.myblog.app.annotation.AccessLimit;
import com.xjh.myblog.common.utils.AopUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(0)
public class AccessLimitAspect {

    @Autowired
    private AccessLimitService accessLimitService;

    @Pointcut(value = "execution(* com.xjh.myblog.app.controller.*.*(..))" +
            "&& @annotation(com.xjh.myblog.app.annotation.AccessLimit)")
    public void pointCut(){}

    @Before("pointCut()")
    public void accessLimitHandle(JoinPoint joinPoint) {
        // 获取注解
        AccessLimit accessLimit = AopUtil.getAnnotation(joinPoint,AccessLimit.class);
        accessLimitService.doAccessLimit(accessLimit);
    }
}
