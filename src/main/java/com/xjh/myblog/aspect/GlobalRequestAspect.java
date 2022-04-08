package com.xjh.myblog.aspect;

import com.xjh.myblog.annotation.UnAccessLimit;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.utils.AopUtil;
import com.xjh.myblog.utils.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

// 全局接口请求切面
@Component
@Aspect
@Order(-1)
public class GlobalRequestAspect {

    @Autowired
    private AccessLimitAspect accessLimitAspect;

    @Pointcut(value = "execution(* com.xjh.myblog.controller.*.*(..))"+ "&& (" +
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
        // 首先获取ip地址
        String ipAddr = ServletUtil.getRequestIpAddr();
        if(ipAddr == null){
            throw new MyException("无法获取请求的ip地址,视为恶意请求,拒绝服务");
        }
        accessLimitAspect.interceptBlackIp(ipAddr);
    }
}
