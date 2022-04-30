package com.xjh.myblog.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// 用于限制恶意刷接口
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    // 时间窗口期    默认8s
    long period() default 8;
    // 时间窗口期的单位
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    // 时间窗口期内的最大请求数，默认20
    int maxCount() default 20;
}
