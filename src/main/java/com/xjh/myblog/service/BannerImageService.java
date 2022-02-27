package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.BannerImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerImageService extends IService<BannerImage> {
    // 上传banner图片并返回上传后的地址
    String uploadBannerImage(MultipartFile file);

    // 获取所有banner图的地址
    List<String> getBannerImageList();

    // 获取随机一个banner图片返回其地址
    String getRandomBannerImage();
}
