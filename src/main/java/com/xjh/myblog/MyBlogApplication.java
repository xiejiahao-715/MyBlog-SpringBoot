package com.xjh.myblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling // 启用定时任务
public class MyBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBlogApplication.class,args);
    }
}
