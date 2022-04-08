package com.xjh.myblog.task;

import com.xjh.myblog.service.BlogViewCountRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


// 定时同步博客浏览量到数据库的任务
@Component
@Slf4j
public class SyncBlogViewCountTask {
    @Autowired
    private BlogViewCountRedisService blogViewCountRedisService;

    // 每周二的凌晨2点同步一次数据库，
    @Scheduled(cron = "0 0 2 ? * TUE")
    public void sync(){
        blogViewCountRedisService.writeViewCountToDB();
        log.info("同步浏览量成功");
    }
}
