package com.xjh.myblog.app.aspect;

import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.service.BlogViewCountService;
import com.xjh.myblog.common.utils.AopUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
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
    private BlogViewCountService blogViewCountService;

    @Pointcut(value = "execution(* com.xjh.myblog.app.controller.BlogPublicController.getPublishedBlogContentById(Long,*)) " +
            "&& @annotation(com.xjh.myblog.app.annotation.CountBlogViews)")
    public void pointCut(){}

    @After("pointCut()")
    public void countBlogViews(JoinPoint point){
        // 获取请求接口传入的博客id
        Long blogId = AopUtil.getArgs(point,0,Long.class);
        blogViewCountService.increaseBlogViews(blogId);
    }
}
