package com.xjh.myblog.app.task;

import com.xjh.myblog.app.service.BlogViewCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


// 定时同步博客浏览量到数据库的任务
@Component
@Slf4j
public class SyncBlogViewCountTask {
    @Autowired
    private BlogViewCountService blogViewCountService;

    // 每周二的凌晨2点同步一次数据库，
    @Scheduled(cron = "0 0 2 ? * TUE")
    public void sync(){
        blogViewCountService.writeViewCountToDB();
        log.info("同步浏览量成功");
    }
}
