package com.xjh.myblog.common.service.impl;

import com.xjh.myblog.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public boolean hasKey(String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    @Override
    public boolean setnx(String key,Object value,Long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key,value,timeout,unit);
        return result != null && result;
    }

    @Override
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key,value,timeout);
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public boolean del(String key){
        Boolean result = redisTemplate.delete(key);
        return result != null && result;
    }

    @Override
    public long del(Collection<String> keys) {
        Long result = redisTemplate.delete(keys);
        return result == null ? 0L : result;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) return null;
        return clazz.cast(value);
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public boolean expire(String key, Long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.expire(key,timeout,unit);
        return result != null && result;
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    @Override
    public void hyperLogLogAdd(String key, Object value) {
        redisTemplate.opsForHyperLogLog().add(key,value);
    }

    @Override
    public Long hyperLogLogCount(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}
