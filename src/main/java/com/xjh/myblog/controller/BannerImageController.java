package com.xjh.myblog.controller;

import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.service.BannerImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bannerImage")
@CrossOrigin
public class BannerImageController {
    @Autowired
    private BannerImageService bannerImageService;

    @PostMapping
    public IResult uploadBannerImage(@RequestParam("file")MultipartFile file){
        String src = bannerImageService.uploadBannerImage(file);
        return IResult.success().data("src",src);
    }

    @GetMapping
    public IResult getRandomBannerImage(){
        String src = bannerImageService.getRandomBannerImage();
        return IResult.success().data("src",src);
    }
}
