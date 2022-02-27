package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.BlogCategory;

import java.util.List;

public interface BlogCategoryService extends IService<BlogCategory> {
    List<BlogCategory> getBlogCategories();
    // 判断一个分类的id是否存在
    Boolean isCategoryIdExist(Long id);
}
