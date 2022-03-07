package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.BannerImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerImageService extends IService<BannerImage> {
    /**
     * 上传banner图片
     * @param file 图片文件
     * @return 返回上传后图片的地址
     */
    String uploadBannerImage(MultipartFile file);

    /**
     * @return 获取BannerImage类型的图像列表
     */
    List<BannerImage> getBannerImageList();

    /**
     * @return 获取随机一个banner图片返回其地址
     */
    String getRandomBannerImage();

    /**
     * 根据图片id删除一张banner图
     * @param id 图片id
     * @return 是否删除成功
     */
    boolean deleteBannerImage(Long id);
}
