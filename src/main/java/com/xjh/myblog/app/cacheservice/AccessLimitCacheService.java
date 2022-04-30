package com.xjh.myblog.app.cacheservice;

import com.xjh.myblog.app.annotation.AccessLimit;

public interface AccessLimitCacheService {

    /**
     * 利用 缓存 来检查此处请求是否能够通过
     * 如果不能通过则会封如黑名单,并返回false
     * 如果通过则会放行,且会记录此处访问
     * 当处理过程中抛出异常时，会返回null
     * @param ip 请求者ip
     * @param accessLimitConfig 配置请求频率的限制
     */
    Boolean checkAccess(String ip,AccessLimit accessLimitConfig);

    /**
     * 查看该ip是否被封禁到黑名单中
     * @param ip 要查看的ip
     * @return 如果存在则会返回true,不存在黑名单中则会返回false
     */
    Boolean isBlackIp(String ip);

    /**
     * 解封ip,也就是从黑名单中移除（从缓存中删除对应的key）
     */
    void enableIpAccess(String ip);

    /**
     * 设置 验证码 到缓存中，只有当ip被封如黑名单，请求解封的时候才会调用此方法
     * @param ip 请求解封的ip
     * @param code 想要存入的验证码
     */
    void setVerifyCode(String ip,String code);

    /**
     * 获取 存储在缓存中的验证码 只有当ip被封如黑名单，请求解封的时候才会调用此方法
     * @param ip 请求解封的ip
     */
    String getVerifyCode(String ip);

    /**
     * 删除 缓存中的验证码
     * @param ip 请求解封的ip
     */
    void delVerifyCode(String ip);
}
