package com.xjh.myblog.app.cacheservice.impl;

import com.xjh.myblog.app.annotation.AccessLimit;
import com.xjh.myblog.app.annotation.CacheException;
import com.xjh.myblog.app.cacheservice.AccessLimitCacheService;
import com.xjh.myblog.common.service.RedisService;
import com.xjh.myblog.common.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class AccessLimitCacheServiceImpl implements AccessLimitCacheService {
    @Autowired
    private RedisService redisService;

    // ip黑名单的redisKey的前缀，当访问超过一定频率会加入黑名单
    private final static String ipBlacklistPrefix = "ipBlacklist-";
    // ip黑名单的过期时间，默认 1 天
    private final static Duration ipBlacklistTTL = Duration.ofDays(1);

    // 有关用户访问一个 加上限制注解的接口时，添加的redisKey的前缀，通过该key来记录请求的接口路径，以及ip地址，格式为 prefix+路径+ip
    private final static String ipAccessLimitRecordPrefix = "ipAccessLimitRecord-";

    // 存储 解决访问限制时(将ip从黑名单移除时) 的验证码的redisKey的前缀
    private final static String unAccessLimitVerifyCodePrefix = "unAccessLimitVerifyCodeKey-";
    private final static Duration unAccessLimitVerifyCodeTTL = Duration.ofMinutes(1);

    // 使用到的RedisKey的key值构造函数
    private String getIpBlackKey(String ip){
        return ipBlacklistPrefix + ip;
    }
    private String getUnAccessLimitVerifyCodeKey(String ip){
        return unAccessLimitVerifyCodePrefix + ip;
    }
    private String getIpAccessLimitKey(String ip,String method,String uri){
        return ipAccessLimitRecordPrefix + method + "-" + uri + "-"+ ip;
    }

    @Override
    public Boolean checkAccess(String ip,AccessLimit config) {
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ipAccessLimitKey = getIpAccessLimitKey(ip,method,uri);
        // 尝试重置访问次数
        boolean initCount= redisService.setnx(ipAccessLimitKey,0,config.period(),config.timeUnit());
        // 自增操作
        Long count = redisService.incr(ipAccessLimitKey);
        // 当初始化访问次数失败后检查过期时间
        // 防止出现：当setnx操作失败后对应的key恰好过期，随后调用incr命令自增使对应的key值无法过期的情况
        if(!initCount){
            Long expire = redisService.getExpire(ipAccessLimitKey);
            if(expire != null && expire == -1){
                // 重新设置过期时间 保证key可以正常过期
                redisService.expire(ipAccessLimitKey,config.period(),config.timeUnit());
            }
        }
        if(count == null){ // 如果返回null，则代表 自增的 命令执行错误;
            return null;
        }
        if(count >= config.maxCount()){
            // 加入ip到黑名单中
            String ipBlackKey = getIpBlackKey(ip);
            redisService.set(ipBlackKey,"",ipBlacklistTTL);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public Boolean isBlackIp(String ip) {
        String ipBlackKey = getIpBlackKey(ip);
        return redisService.hasKey(ipBlackKey);
    }

    @Override
    @CacheException
    public void setVerifyCode(String ip,String code) {
        String unAccessLimitVerifyCodeKey = getUnAccessLimitVerifyCodeKey(ip);
        redisService.set(unAccessLimitVerifyCodeKey,code,unAccessLimitVerifyCodeTTL);
    }

    @Override
    @CacheException
    public String getVerifyCode(String ip) {
        String unAccessLimitVerifyCodeKey = getUnAccessLimitVerifyCodeKey(ip);
        return redisService.get(unAccessLimitVerifyCodeKey,String.class);
    }

    @Override
    public void delVerifyCode(String ip) {
        String unAccessLimitVerifyCodeKey = getUnAccessLimitVerifyCodeKey(ip);
        redisService.del(unAccessLimitVerifyCodeKey);
    }

    @Override
    @CacheException
    public void enableIpAccess(String ip) {
        String ipBlackKey = getIpBlackKey(ip);
        redisService.del(ipBlackKey);
    }
}
