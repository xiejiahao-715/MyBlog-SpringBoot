package com.xjh.myblog.aspect;


import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.service.BlogViewCountRedisService;
import com.xjh.myblog.utils.AopUtil;
import com.xjh.myblog.utils.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(2)
public class CountBlogViewsAspect {

    @Autowired
    private BlogViewCountRedisService blogViewCountRedisService;

    @Pointcut(value = "execution(* com.xjh.myblog.controller.BlogPublicController.getPublishedBlogContentById(Long,*)) " +
            "&& @annotation(com.xjh.myblog.annotation.CountBlogViews)")
    public void pointCut(){}

    @Before("pointCut()")
    public void countBlogViews(JoinPoint point){
        // 获取请求接口传入的博客id
        Long blogId = AopUtil.getArgs(point,0,Long.class);
        if(blogId == null || blogId < 0){
            throw new MyException("博客id不合法");
        }
        String ipAddr = ServletUtil.getRequestIpAddr();
        blogViewCountRedisService.increaseBlogViews(blogId,ipAddr);
    }
}
