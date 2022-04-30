package com.xjh.myblog.app.controller;

import com.xjh.myblog.app.annotation.TokenPermission;
import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.app.utils.OssPropertiesUtil;
import com.xjh.myblog.app.entity.Social;
import com.xjh.myblog.app.entity.WebsiteInfo;
import com.xjh.myblog.app.entity.vo.WebsiteInfoVo;
import com.xjh.myblog.app.service.SocialService;
import com.xjh.myblog.app.service.WebsiteInfoService;
import com.xjh.myblog.common.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "网站基本信息相关接口")
@RestController
@CrossOrigin
@RequestMapping("/info")
public class WebsiteInfoController {

    @Autowired
    private WebsiteInfoService websiteInfoService;
    @Autowired
    private SocialService socialService;

    @ApiOperation("获取网站基本信息")
    @GetMapping("/website")
    public CommonResult getWebsiteInfo(@RequestParam(value = "id",required = false,defaultValue = "1")Integer id){
        WebsiteInfo info = websiteInfoService.getWebsiteInfoById(id);
        // 修改返回时的图像路径 数据库存储的都是资源在oss中的名称 不含前缀域名
        if(StringUtil.notNull(info.getAvatar())) {
            info.setAvatar(OssPropertiesUtil.RESOURCE_DOMAIN + "/" + info.getAvatar());
        }
        return CommonResult.success().data("info",info);
    }

    @ApiOperation("获取社交信息(联系方式)")
    @GetMapping("/socials")
    public CommonResult getSocials(){
        List<Social> socials = socialService.getSocials();
        return CommonResult.success().data("socials",socials);
    }

    @ApiOperation("修改网站信息")
    @PostMapping("/website")
    @TokenPermission
    public CommonResult updateWebsiteInfoById(@Valid @RequestBody WebsiteInfoVo websiteInfoVo){
        boolean isSuccess = websiteInfoService.updateWebsiteInfoById(websiteInfoVo);
        return isSuccess ? CommonResult.success().message("修改网站信息成功") : CommonResult.error().message("修改网站信息失败");
    }
}
