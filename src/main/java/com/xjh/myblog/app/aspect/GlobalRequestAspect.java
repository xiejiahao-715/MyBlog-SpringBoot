package com.xjh.myblog.app.aspect;

import com.xjh.myblog.app.annotation.UnAccessLimit;
import com.xjh.myblog.app.service.AccessLimitService;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.common.utils.AopUtil;
import com.xjh.myblog.common.utils.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// 全局接口请求切面
@Component
@Aspect
@Order(-1)
public class GlobalRequestAspect {

    @Autowired
    private AccessLimitService accessLimitService;

    @Pointcut(value = "execution(* com.xjh.myblog.app.controller.*.*(..))"+ "&& (" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)  || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) ||"+
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) ||"+
            "@annotation(org.springframework.web.bind.annotation.PutMapping) )")
    public void pointCut(){}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        // 查看该接口是否不需要拦截,标注了注解的直接放行
        UnAccessLimit unAccessLimit = AopUtil.getAnnotation(joinPoint,UnAccessLimit.class);
        if(unAccessLimit != null) return;
        accessLimitService.interceptBlackIp();
    }
}
