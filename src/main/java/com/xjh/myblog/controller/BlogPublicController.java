package com.xjh.myblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjh.myblog.annotation.AccessLimit;
import com.xjh.myblog.annotation.CountBlogViews;
import com.xjh.myblog.annotation.UnAccessLimit;
import com.xjh.myblog.aspect.AccessLimitAspect;
import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.service.BlogPublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "博客公共信息接口")
@RestController
@CrossOrigin
@RequestMapping("/blog")
public class BlogPublicController {
    @Autowired
    private BlogPublicService blogPublicService;
    @Autowired
    private AccessLimitAspect accessLimitAspect;

    @ApiOperation("下载博客(.zip)")
    @GetMapping("/download")
    @AccessLimit(maxCount = 5)
    public void downloadBlogFileZip(HttpServletResponse response, @RequestParam("id") Long blogId){
        blogPublicService.downloadBlogZip(blogId,response);
    }

    @ApiOperation("获取已发布博客的信息(分页查询)")
    @GetMapping("/published/page")
    @AccessLimit
    public IResult getPublishedBlogPage(
            @RequestParam(required = false,value = "current",defaultValue = "1") Long current,
            @RequestParam(required = false,value = "limit",defaultValue = "5") Long limit,
            @RequestParam(required = false,value = "category",defaultValue = "") Long categoryId){
        Page<Blog> blogPage = blogPublicService.getPublishedBlogPage(current,limit,categoryId);
        Map<String,Object> data = new HashMap<>();
        data.put("blog",blogPage.getRecords());
        data.put("hasMore",blogPage.hasNext());
        data.put("total",blogPage.getTotal());
        return IResult.success().data(data);
    }

    @ApiOperation("根据blogId获取已发布博客的信息")
    @GetMapping("/published/info")
    @AccessLimit
    public IResult getPublishedBlogById(@RequestParam("id") Long id){
        Blog blog = blogPublicService.getPublishedBlogById(id);
        return IResult.success().data("blog",blog);
    }

    @ApiOperation("获取已发布博客的内容")
    @GetMapping("/published/content")
    @CountBlogViews
    @AccessLimit
    public void getPublishedBlogContentById(@RequestParam("id")Long id,HttpServletResponse response){
        blogPublicService.getPublishedBlogContentById(id,response);
    }

    @ApiOperation("获取文章归档信息")
    @GetMapping("/blogArchives")
    @AccessLimit
    public IResult getBlogArchives(
            @RequestParam(name = "type",required = false,defaultValue = "list")String archivesType){
        if(archivesType.equals("list")){
            return IResult.success().data("archives",blogPublicService.getBlogArchiveList());
        }else if(archivesType.equals("tree")){
            return IResult.success().data("archives",blogPublicService.getBlogArchiveTree());
        }else{
            throw new MyException("服务器内部异常");
        }
    }

    @ApiOperation("获取需要解封IP的验证码")
    @GetMapping("/unAccessLimit/code")
    @UnAccessLimit
    public IResult getUnAccessLimitCode(HttpServletResponse response){
        String imageSrc = accessLimitAspect.getUnAccessLimitCode();
        return IResult.success().data("src",imageSrc);
    }

    @ApiOperation("解决IP限制的接口")
    @GetMapping("/unAccessLimit/verify")
    @UnAccessLimit
    public IResult verifyUnAccessLimitCode(@RequestParam("code")String code){
        boolean isSuccess = accessLimitAspect.verifyUnAccessLimitCode(code);
        return isSuccess ? IResult.success().message("解除IP限制成功") : IResult.error().message("验证码错误");
    }
}
