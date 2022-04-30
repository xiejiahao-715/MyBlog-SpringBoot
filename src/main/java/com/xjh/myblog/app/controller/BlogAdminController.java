package com.xjh.myblog.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjh.myblog.app.annotation.TokenPermission;
import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.app.entity.Blog;
import com.xjh.myblog.app.entity.vo.BlogVo;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.service.BlogAdminService;
import com.xjh.myblog.app.service.BlogViewCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "管理员管理博客接口")
@RestController
@RequestMapping("/blogManage")
@CrossOrigin
@Slf4j
public class BlogAdminController {
    @Autowired
    private BlogAdminService blogAdminService;

    @Autowired
    private BlogViewCountService blogViewCountService;

    @ApiOperation("创建博客")
    @PostMapping("/create")
    @TokenPermission
    public CommonResult createBlog(@Valid @RequestBody BlogVo blogVo){
        Long id = blogAdminService.createBlog(blogVo);
        return CommonResult.success()
                .message("创建博客成功")
                .data("id",id);
    }

    @ApiOperation("获取博客基本信息")
    @GetMapping("/basicInfo")
    @TokenPermission
    public CommonResult getBlogBasicInfoById(@RequestParam("id") Long id){
        BlogVo blogVo = blogAdminService.getBlogBasicInfoById(id);
        return CommonResult.success()
                .data("info",blogVo);
    }

    @ApiOperation("修改博客的基本信息")
    @PostMapping("/basicInfo")
    @TokenPermission
    public CommonResult updateBlogBasicInfo(
            @RequestParam("id")Long id,
            @Valid @RequestBody BlogVo blogVo){
        blogVo.setId(id);
        if(blogAdminService.updateBlogBasicInfo(blogVo)){
            return CommonResult.success();
        }else{
            return CommonResult.error();
        }
    }

    @ApiOperation("上传博客的封面图片")
    @PostMapping("/upload/coverImage")
    @TokenPermission
    public CommonResult uploadCoverImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String imageUrl = blogAdminService.uploadBlogCoverImage(id,file);
        return CommonResult.success().data("url",imageUrl);
    }

    @ApiOperation("上传博客内容中的图片")
    @PostMapping("/upload/image")
    @TokenPermission
    public CommonResult uploadImage(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        String url = blogAdminService.uploadImage(id,file);
        return CommonResult.success().data("url",url);
    }

    @ApiOperation("上传博客的内容")
    @PostMapping("/upload/content")
    @TokenPermission
    public CommonResult uploadBlogContent(
            @RequestParam("id") Long id,
            @RequestParam("file") MultipartFile file){
        if(blogAdminService.uploadBlogContent(id,file)){
            return CommonResult.success().message("保存博客内容成功");
        }else {
            return CommonResult.error().message("保存博客内容失败");
        }
    }

    @ApiOperation("获取所有状态的博客(分页查询)")
    @GetMapping("/page/all")
    @TokenPermission
    public CommonResult getBlogPageAllStatus(
            @RequestParam(required = false,value = "current",defaultValue = "1") Long current,
            @RequestParam(required = false,value = "limit",defaultValue = "5") Long limit){
        Page<Blog> blogPage = blogAdminService.getBlogPageAllStatus(current,limit);
        Map<String,Object> data = new HashMap<>();
        data.put("blog",blogPage.getRecords());
        data.put("hasMore",blogPage.hasNext());
        data.put("total",blogPage.getTotal());
        return CommonResult.success().data(data);
    }

    @ApiOperation("发布博客")
    @GetMapping("/publish")
    @TokenPermission
    public CommonResult publishBlog(@RequestParam("id") Long id){
        if(blogAdminService.publishBlog(id)){
            return CommonResult.success().message("发布博客成功");
        }else {
            return CommonResult.error().message("发布博客失败");
        }
    }

    @ApiOperation("获取博客内容,直接以二进制流的形式写入响应头")
    @GetMapping("/content")
    @TokenPermission
    public void getBlogContent(HttpServletResponse response, @RequestParam("id") Long blogId){
        blogAdminService.getBlogContent(blogId,response);
    }

    @ApiOperation("取消博客发布")
    @DeleteMapping("/cancelPublish")
    @TokenPermission
    public CommonResult cancelPublishBlog(@RequestParam("id") Long id){
        return blogAdminService.cancelPublishBlog(id)
                ? CommonResult.success().message("取消博客发布成功") : CommonResult.error().message("取消博客失败");
    }

    @ApiOperation("强制更新浏览量到数据库，清空统计周期")
    @GetMapping("/forceSyncViewCounts")
    @TokenPermission
    public CommonResult forceSyncViewCounts(){
        try {
            blogViewCountService.writeViewCountToDB();
            log.info("管理员强制更新浏览量成功");
            return CommonResult.success().message("强制更新浏览量成功");
        } catch (MyException myException){
            log.error("管理员强制更新浏览量失败");
            return CommonResult.error().message("强制更新浏览量失败");
        }
    }
}
