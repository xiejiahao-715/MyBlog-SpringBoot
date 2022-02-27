package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.annotation.TokenPermission;
import com.xjh.myblog.entity.BlogCategory;
import com.xjh.myblog.mapper.BlogCategoryMapper;
import com.xjh.myblog.service.BlogCategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogCategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory> implements BlogCategoryService {
    @Override
    @Cacheable(value = "category",key = "'list'")
    @Transactional(readOnly = true)
    public List<BlogCategory> getBlogCategories() {
        return this.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isCategoryIdExist(Long id) {
        if(id==null) return false;
        QueryWrapper<BlogCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        return count(wrapper) == 1;
    }
}
