package com.xjh.myblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjh.myblog.annotation.TokenPermission;
import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.entity.vo.BlogVo;
import com.xjh.myblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    // 获取博客内容(.md文件) 直接以二进制流的形式返回给前端
    @GetMapping("/content")
    @TokenPermission
    public void getBlogContent(HttpServletResponse response, @RequestParam("id") Long blogId){
        blogService.getBlogContent(blogId,response);
    }
    @GetMapping("/download")
    public void downloadBlogFileZip(HttpServletResponse response, @RequestParam("id") Long blogId){
        blogService.downloadBlogZip(blogId,response);
    }

    // 创建一个新的博客并返回该博客的id
    @PostMapping("/create")
    @TokenPermission
    public IResult createBlog(@RequestBody BlogVo blogVo){
        System.out.println(blogVo);
        Long id = blogService.createBlog(blogVo);
        return IResult.success()
                .message("创建博客成功")
                .data("id",id);
    }

    @GetMapping("/basicInfo")
    @TokenPermission
    public IResult getBlogBasicInfoById(@RequestParam("id") Long id){
        BlogVo blogVo = blogService.getBlogBasicInfoById(id);
        return IResult.success()
                .data("info",blogVo);
    }

    @PostMapping("/basicInfo")
    @TokenPermission
    public IResult updateBlogBasicInfo(
            @RequestParam("id")Long id,
            @RequestBody BlogVo blogVo){
        blogVo.setId(id);
        if(blogService.updateBlogBasicInfo(blogVo)){
            return IResult.success();
        }else{
            return IResult.error();
        }
    }

    // 上传博客的封面图片
    @PostMapping("/upload/coverImage")
    @TokenPermission
    public IResult uploadCoverImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String imageUrl = blogService.uploadBlogCoverImage(id,file);
        return IResult.success().data("url",imageUrl);
    }
    // 上传博客内容中的图片
    @PostMapping("/upload/image")
    @TokenPermission
    public IResult uploadImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String url = blogService.uploadImage(id,file);
        return IResult.success().data("url",url);
    }

    @PostMapping("/upload/content")
    @TokenPermission
    public IResult uploadBlogContent(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        if(blogService.uploadBlogContent(id,file)){
            return IResult.success();
        }else {
            return IResult.error();
        }
    }
    @GetMapping("/page/all")
    @TokenPermission
    public IResult getBlogPageAllStatus(
            @RequestParam(required = false,value = "current",defaultValue = "1") Long current,
            @RequestParam(required = false,value = "limit",defaultValue = "5") Long limit){
        Page<Blog> blogPage = blogService.getBlogPageAllStatus(current,limit);
        Map<String,Object> data = new HashMap<>();
        data.put("blog",blogPage.getRecords());
        data.put("hasMore",blogPage.hasNext());
        data.put("total",blogPage.getTotal());
        return IResult.success().data(data);
    }

    @GetMapping("/publish")
    @TokenPermission
    public IResult publishBlog(@RequestParam("id") Long id){
        if(blogService.publishBlog(id)){
            return IResult.success().message("发布博客成功");
        }else {
            return IResult.error().message("发布博客失败");
        }
    }

    // 获取已发布博客的信息(分页查询)
    @GetMapping("/published/page")
    public IResult getPublishedBlogPage(
            @RequestParam(required = false,value = "current",defaultValue = "1") Long current,
            @RequestParam(required = false,value = "limit",defaultValue = "5") Long limit,
            @RequestParam(required = false,value = "category",defaultValue = "") Long categoryId){
        Page<Blog> blogPage = blogService.getPublishedBlogPage(current,limit,categoryId);
        Map<String,Object> data = new HashMap<>();
        data.put("blog",blogPage.getRecords());
        data.put("hasMore",blogPage.hasNext());
        data.put("total",blogPage.getTotal());
        return IResult.success().data(data);
    }
    // 根据blogId获取已发布博客的信息
    @GetMapping("/published/info")
    public IResult getPublishedBlogById(@RequestParam("id") Long id){
        Blog blog = blogService.getPublishedBlogById(id);
        return IResult.success().data("blog",blog);
    }

    // 获取已发布博客的内容
    @GetMapping("/published/content")
    public void getPublishedBlogContentById(@RequestParam("id")Long id,HttpServletResponse response){
        blogService.getPublishedBlogContentById(id,response);
    }
}
