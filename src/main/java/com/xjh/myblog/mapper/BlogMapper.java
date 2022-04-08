package com.xjh.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjh.myblog.entity.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper extends BaseMapper<Blog> {
    int updateBlogViewCountBatchById(@Param("blogList") List<Blog> blogList);
}
