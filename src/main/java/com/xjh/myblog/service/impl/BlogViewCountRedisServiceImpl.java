package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.BlogMapper;
import com.xjh.myblog.service.BlogViewCountRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlogViewCountRedisServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogViewCountRedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    // 定义存储浏览量redisKey的格式
    public final static String prefix = "blogViews";
    public final static String link = "-";

    // 获得存储博客访问量的redisKey
    private String getBlogViewsRedisKey(Long blogId){
        return String.format("%s%s%s",prefix,link,blogId);
    }

    // 判断是否存在 博客浏览器 的redis缓存
    private boolean hasKey(String key){
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    // 增加博客的浏览量
    @Override
    @Transactional(readOnly = true)
    public void increaseBlogViews(Long blogId,String ipAddr) throws MyException{
        String key = getBlogViewsRedisKey(blogId);
        if(!hasKey(key) && this.count(new QueryWrapper<Blog>().eq("id",blogId)) == 0){
            throw new MyException("博客id不存在");
        }
        // 记录该此博客的访问
        redisTemplate.opsForHyperLogLog().add(key,ipAddr);
    }

    // 设置博客的访问量
    @Override
    public void setBlogViewCounts(Blog blog){
        String key = getBlogViewsRedisKey(blog.getId());
        if(hasKey(key)) {
            blog.setViewCount(BigInteger.valueOf(redisTemplate.opsForHyperLogLog().size(key)).add(blog.getViewCount()));
        }
    }

    // 写入浏览量到数据库
    @Override
    @Transactional
    public void writeViewCountToDB(){
        Set<String> redisKeys = redisTemplate.keys(prefix + link + "*");
        if(redisKeys!=null && !redisKeys.isEmpty()) {
            // 获取所有的key-value
            List<Blog> blogList = new ArrayList<>();
            // 利用正则表达式匹配出博客id
            String regex = String.format("^%s(\\d*)$", prefix + link);
            Pattern pattern = Pattern.compile(regex);
            for (String key : redisKeys) {
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    // 获取博客id
                    Long id = Long.valueOf(matcher.group(1));
                    // 博客对应的浏览量
                    BigInteger viewCounts = BigInteger.valueOf(redisTemplate.opsForHyperLogLog().size(key));
                    Blog blog = new Blog();
                    blog.setId(id);
                    blog.setViewCount(viewCounts);
                    blogList.add(blog);
                }
            }
            // 导入数据库并删除对应的redisKey，重新计算周期
            if(this.baseMapper.updateBlogViewCountBatchById(blogList) != blogList.size()
                && Objects.requireNonNull(redisTemplate.delete(redisKeys)).intValue() != redisKeys.size()){
                throw new MyException("导入博客浏览量失败");
            }
        }
    }
}
