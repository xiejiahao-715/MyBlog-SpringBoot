package com.xjh.myblog.controller;

import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.constant.OssProperties;
import com.xjh.myblog.entity.Social;
import com.xjh.myblog.entity.WebsiteInfo;
import com.xjh.myblog.service.SocialService;
import com.xjh.myblog.service.WebsiteInfoService;
import com.xjh.myblog.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/info")
public class WebsiteInfoController {

    @Autowired
    WebsiteInfoService websiteInfoService;
    @Autowired
    SocialService socialService;

    // 获取博客网站的基本信息
    @GetMapping("/website")
    public IResult getWebsiteInfo(@RequestParam(value = "id",required = false,defaultValue = "1")Integer id){
        WebsiteInfo info = websiteInfoService.getWebsiteInfoById(id);
        // 修改返回时的图像路径 数据库存储的都是资源在oss中的名称 不含前缀域名
        if(StringUtil.notNull(info.getAvatar())) {
            info.setAvatar(OssProperties.RESOURCE_DOMAIN + "/" + info.getAvatar());
        }
        return IResult.success().data("info",info);
    }
    // 获取社交信息(联系方式)
    @GetMapping("/socials")
    public IResult getSocials(){
        List<Social> socials = socialService.getSocials();
        return IResult.success().data("socials",socials);
    }
}
