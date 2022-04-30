package com.xjh.myblog.app.cacheservice;

// 博客浏览量的缓存操作
public interface BlogViewCountCacheService {

    /**
     * 增加缓存中博客的浏览量
     * @param id 博客的id
     */
    void incrBlogView(Long id);

    /**
     * 获取缓存中博客的浏览量
     * @param id 博客的id
     * @return 返回浏览量，若缓存中不存在会返回0,如果操作redis报错则会返回null
     */
    Long getCacheCount(Long id);

    /**
     * 获取存储时的redisKey的前缀
     */
    String getKeyPrefix();

}
