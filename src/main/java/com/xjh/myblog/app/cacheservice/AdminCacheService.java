package com.xjh.myblog.app.cacheservice;

// 管理员接口相关的缓存服务
public interface AdminCacheService {
    /**
     * 设置管理员的token缓存
     * @param uid 管理员的id
     * @param token token字符串
     */
    void setToken(String uid,String token);

    /**
     * 删除管理员的token缓存
     * @param uid 管理员的id
     */
    void delToken(String uid);

    /**
     * 获取token值
     * @param uid 管理员的id
     * @return 返回token的值，如果不存在则会返回null
     */
    String getToken(String uid);
}
