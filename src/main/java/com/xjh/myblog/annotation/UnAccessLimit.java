package com.xjh.myblog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// 用于用户验证解除ip限制
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnAccessLimit {
}
