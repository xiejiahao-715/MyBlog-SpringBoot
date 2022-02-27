package com.xjh.myblog.controller;

import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.BlogCategory;
import com.xjh.myblog.service.BlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/blogCategory")
public class BlogCategoryController {
    @Autowired
    BlogCategoryService blogCategoryService;

    @GetMapping
    public IResult getBlogCategories(){
        List<BlogCategory> categories = blogCategoryService.getBlogCategories();
        return IResult.success().data("categories",categories);
    }
}
