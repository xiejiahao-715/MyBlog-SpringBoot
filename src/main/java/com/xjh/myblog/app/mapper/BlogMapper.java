package com.xjh.myblog.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjh.myblog.app.entity.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper extends BaseMapper<Blog> {
    int updateBlogViewCountBatchById(@Param("blogList") List<Blog> blogList);
}
