package com.xjh.myblog.app.cacheservice.impl;

import com.xjh.myblog.app.cacheservice.BlogViewCountCacheService;
import com.xjh.myblog.common.service.RedisService;
import com.xjh.myblog.common.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlogViewCountCacheServiceImpl implements BlogViewCountCacheService {

    @Autowired
    private RedisService redisService;

    // 定义存储浏览量redisKey的格式
    private final static String PREFIX = "blogViews-";

    private String getBlogViewCountKey(String blogId){
        return PREFIX + blogId;
    }


    // 增加博客的浏览量
    @Override
    public void incrBlogView(Long id){
        String ip = ServletUtil.getRequestIpAddr();
        String key = getBlogViewCountKey(id.toString());
        redisService.hyperLogLogAdd(key,ip);
    }

    // 获取已经缓存的访问量
    @Override
    public Long getCacheCount(Long id){
        String key = getBlogViewCountKey(id.toString());
        Long count = redisService.hyperLogLogCount(key);
        return  count == null ? 0 : count;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }
}
