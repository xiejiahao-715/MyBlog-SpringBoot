package com.xjh.myblog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// 通过AOP来实现博客的浏览量记录  存储在 redis中，采用HyperLogLog类型存储
// 默认生成的redis的key值为 ${prefix}${joiner}${blogId}
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountBlogViews {
}
