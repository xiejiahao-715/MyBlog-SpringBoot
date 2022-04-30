package com.xjh.myblog.app.controller;

import com.xjh.myblog.app.annotation.TokenPermission;
import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.app.entity.BannerImage;
import com.xjh.myblog.app.service.BannerImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "网站的banner图片相关接口")
@RestController
@RequestMapping("/bannerImage")
@CrossOrigin
public class BannerImageController {
    @Autowired
    private BannerImageService bannerImageService;

    @ApiOperation("上传banner图")
    @PostMapping
    @TokenPermission
    public CommonResult uploadBannerImage(@RequestParam("file")MultipartFile file){
        String src = bannerImageService.uploadBannerImage(file);
        return CommonResult.success().data("src",src);
    }

    @ApiOperation("删除一张banner图")
    @DeleteMapping
    @TokenPermission
    public CommonResult deleteBannerImage(@RequestParam("id")Long id){
        boolean isDeleted = bannerImageService.deleteBannerImage(id);
        return isDeleted ? CommonResult.success().message("删除banner图成功") : CommonResult.error().message("删除banner图失败");
    }

    @ApiOperation("随机获取一张banner图的链接")
    @GetMapping
    public CommonResult getRandomBannerImage(){
        String src = bannerImageService.getRandomBannerImage();
        return CommonResult.success().data("src",src);
    }

    @ApiOperation("获取所有banner图片")
    @GetMapping("/list")
    @TokenPermission
    public CommonResult getBannerImageList(){
        List<BannerImage> banners = bannerImageService.getBannerImageList();
        return CommonResult.success().data("banners",banners);
    }
}
