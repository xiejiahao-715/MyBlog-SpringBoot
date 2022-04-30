package com.xjh.myblog.app.controller;

import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.app.entity.BlogCategory;
import com.xjh.myblog.app.service.BlogCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "博客分类相关接口")
@RestController
@CrossOrigin
@RequestMapping("/blogCategory")
public class BlogCategoryController {
    @Autowired
    private BlogCategoryService blogCategoryService;

    @ApiOperation("获取博客分类列表")
    @GetMapping
    public CommonResult getBlogCategories(){
        List<BlogCategory> categories = blogCategoryService.getBlogCategories();
        return CommonResult.success().data("categories",categories);
    }
}
