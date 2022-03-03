package com.xjh.myblog.controller;

import com.xjh.myblog.annotation.TokenPermission;
import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.service.BannerImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public IResult uploadBannerImage(@RequestParam("file")MultipartFile file){
        String src = bannerImageService.uploadBannerImage(file);
        return IResult.success().data("src",src);
    }

    @ApiOperation("随机获取一张banner图的链接")
    @GetMapping
    public IResult getRandomBannerImage(){
        String src = bannerImageService.getRandomBannerImage();
        return IResult.success().data("src",src);
    }
}
