package com.xjh.myblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjh.myblog.annotation.TokenPermission;
import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.entity.vo.BlogVo;
import com.xjh.myblog.service.BlogAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "管理员管理博客接口")
@RestController
@RequestMapping("/blogManage")
@CrossOrigin
public class BlogAdminController {
    @Autowired
    private BlogAdminService blogAdminService;

    @ApiOperation("创建博客")
    @PostMapping("/create")
    @TokenPermission
    public IResult createBlog(
            @RequestBody BlogVo blogVo){
        System.out.println(blogVo);
        Long id = blogAdminService.createBlog(blogVo);
        return IResult.success()
                .message("创建博客成功")
                .data("id",id);
    }

    @ApiOperation("获取博客基本信息")
    @GetMapping("/basicInfo")
    @TokenPermission
    public IResult getBlogBasicInfoById(
            @RequestParam("id") Long id){
        BlogVo blogVo = blogAdminService.getBlogBasicInfoById(id);
        return IResult.success()
                .data("info",blogVo);
    }

    @ApiOperation("修改博客的基本信息")
    @PostMapping("/basicInfo")
    @TokenPermission
    public IResult updateBlogBasicInfo(
            @RequestParam("id")Long id,
            @RequestBody BlogVo blogVo){
        blogVo.setId(id);
        if(blogAdminService.updateBlogBasicInfo(blogVo)){
            return IResult.success();
        }else{
            return IResult.error();
        }
    }

    @ApiOperation("上传博客的封面图片")
    @PostMapping("/upload/coverImage")
    @TokenPermission
    public IResult uploadCoverImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String imageUrl = blogAdminService.uploadBlogCoverImage(id,file);
        return IResult.success().data("url",imageUrl);
    }

    @ApiOperation("上传博客内容中的图片")
    @PostMapping("/upload/image")
    @TokenPermission
    public IResult uploadImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String url = blogAdminService.uploadImage(id,file);
        return IResult.success().data("url",url);
    }

    @ApiOperation("上传博客的内容")
    @PostMapping("/upload/content")
    @TokenPermission
    public IResult uploadBlogContent(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        if(blogAdminService.uploadBlogContent(id,file)){
            return IResult.success().message("保存博客内容成功");
        }else {
            return IResult.error().message("保存博客内容失败");
        }
    }

    @ApiOperation("获取所有状态的博客(分页查询)")
    @GetMapping("/page/all")
    @TokenPermission
    public IResult getBlogPageAllStatus(
            @RequestParam(required = false,value = "current",defaultValue = "1") Long current,
            @RequestParam(required = false,value = "limit",defaultValue = "5") Long limit){
        Page<Blog> blogPage = blogAdminService.getBlogPageAllStatus(current,limit);
        Map<String,Object> data = new HashMap<>();
        data.put("blog",blogPage.getRecords());
        data.put("hasMore",blogPage.hasNext());
        data.put("total",blogPage.getTotal());
        return IResult.success().data(data);
    }

    @ApiOperation("发布博客")
    @GetMapping("/publish")
    @TokenPermission
    public IResult publishBlog(@RequestParam("id") Long id){
        if(blogAdminService.publishBlog(id)){
            return IResult.success().message("发布博客成功");
        }else {
            return IResult.error().message("发布博客失败");
        }
    }

    @ApiOperation("获取博客内容,直接以二进制流的形式写入响应头")
    @GetMapping("/content")
    @TokenPermission
    public void getBlogContent(HttpServletResponse response, @RequestParam("id") Long blogId){
        blogAdminService.getBlogContent(blogId,response);
    }
}
