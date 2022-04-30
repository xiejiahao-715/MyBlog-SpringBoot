package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.cacheservice.BlogViewCountCacheService;
import com.xjh.myblog.app.entity.Blog;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.mapper.BlogMapper;
import com.xjh.myblog.app.service.BlogViewCountService;
import com.xjh.myblog.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlogViewCountServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogViewCountService {

    @Autowired
    private BlogViewCountCacheService blogViewCountCacheService;

    @Autowired
    private RedisService redisService;


    // 增加博客的浏览量
    @Override
    public void increaseBlogViews(Long blogId) throws MyException{
        if(blogId != null){
            // 记录该此博客的访问
            blogViewCountCacheService.incrBlogView(blogId);
        }
    }

    // 设置博客的访问量
    @Override
    public void setBlogViewCounts(Blog blog){
        Long cacheCount = blogViewCountCacheService.getCacheCount(blog.getId());
        long temp = cacheCount == null ? 0 : cacheCount;
        blog.setViewCount((BigInteger.valueOf(temp)).add(blog.getViewCount()));
    }

    // 写入浏览量到数据库
    @Override
    @Transactional
    public void writeViewCountToDB(){
        String prefix = blogViewCountCacheService.getKeyPrefix();
        Set<String> redisKeys = redisService.getKeys(prefix + "*");
        if(redisKeys!=null && !redisKeys.isEmpty()) {
            // 获取所有的key-value
            List<Blog> blogList = new ArrayList<>();
            // 利用正则表达式匹配出博客id
            String regex = String.format("^%s(\\d*)$", prefix);
            Pattern pattern = Pattern.compile(regex);
            for (String key : redisKeys) {
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    // 获取博客id
                    Long id = Long.valueOf(matcher.group(1));
                    // 博客对应的浏览量
                    Long cacheCounts = redisService.hyperLogLogCount(key);
                    if(cacheCounts == null){
                        throw new MyException("导入博客浏览量失败");
                    }
                    BigInteger viewCounts = BigInteger.valueOf(cacheCounts);
                    Blog blog = new Blog();
                    blog.setId(id);
                    blog.setViewCount(viewCounts);
                    blogList.add(blog);
                }
            }
            // 导入数据库并删除对应的redisKey，重新计算周期
            if(this.baseMapper.updateBlogViewCountBatchById(blogList) == blogList.size()){
                long delKeysNum = redisService.del(redisKeys);
                if(delKeysNum == redisKeys.size()){
                    return;
                }
            }
            throw new MyException("导入博客浏览量失败");
        }
    }
}
