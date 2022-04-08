package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Blog;

/**
 * 有关记录博客浏览量的服务
 * 博客浏览量采用redis的HyperLogLog类型来存储
 * 浏览量的计算规则是每一个周期一个IP访问算作一次访问量，利用定时任务来删除HyperLogLog来重置周期，并将数据同步到数据库中
 * 周期的设置由定时任务来决定
 * 管理员可以直接重置计算周期，强制同步数据库
 */
public interface BlogViewCountRedisService extends IService<Blog> {
    /**
     * 增加博客浏览量
     * @param blogId 博客id
     * @param ipAddr 请求的ip地址
     */
    void increaseBlogViews(Long blogId,String ipAddr);

    /**
     * 为为Blog对象设置浏览量 = 数据库原有的浏览量+该周期内统计的浏览量
     * @param blog Blog对象
     */
    void setBlogViewCounts(Blog blog);

    /**
     * 同步浏览到数据库，重置计算周期
     */
    void writeViewCountToDB();
}
