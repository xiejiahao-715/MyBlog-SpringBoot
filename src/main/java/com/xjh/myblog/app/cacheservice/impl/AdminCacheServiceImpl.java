package com.xjh.myblog.app.cacheservice.impl;

import com.xjh.myblog.app.annotation.CacheException;
import com.xjh.myblog.app.cacheservice.AdminCacheService;
import com.xjh.myblog.app.utils.TokenUtil;
import com.xjh.myblog.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AdminCacheServiceImpl implements AdminCacheService {

    private final static Duration TOKEN_EXPIRE_TIME = TokenUtil.TOKEN_EXPIRE_TIME;
    private final static String TOKEN_KEY_PREFIX = "token-admin-";

    @Autowired
    private RedisService redisService;

    // 获取存储token的key
    private String getTokenKey(String id){
        return TOKEN_KEY_PREFIX + id;
    }

    @Override
    @CacheException
    public void setToken(String uid,String token) {
        String key = getTokenKey(uid);
        redisService.set(key,token,TOKEN_EXPIRE_TIME);
    }

    @Override
    @CacheException
    public void delToken(String uid) {
        String key = getTokenKey(uid);
        redisService.del(key);
    }

    @Override
    @CacheException
    public String getToken(String uid){
        String key = getTokenKey(uid);
        return redisService.get(key,String.class);
    }
}
