package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.constant.OssProperties;
import com.xjh.myblog.entity.BannerImage;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.BannerImageMapper;
import com.xjh.myblog.service.BannerImageService;
import com.xjh.myblog.service.OssService;
import com.xjh.myblog.utils.FileUtil;
import com.xjh.myblog.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerImageServiceImpl extends ServiceImpl<BannerImageMapper, BannerImage> implements BannerImageService {
    @Autowired
    private OssService ossService;

    // 实现自我注入 @Cacheable注解的方法无法在类内部的其他方法上发挥作用
    @Autowired
    @Lazy
    private BannerImageService bannerImageService;

    @Override
    @Transactional
    @CacheEvict(value = "banner",key = "'list'")
    public String uploadBannerImage(MultipartFile file) {
        // 生成在OOS中的存储路径
        String suffix = FileUtil.getSuffix(file);
        String filename = UUIDUtil.getUUID32();
        String path = "public/banner-image/"+ filename +suffix;
        String src = OssProperties.RESOURCE_DOMAIN+"/"+path;
        // 写入数据库
        BannerImage bannerImage = new BannerImage();
        bannerImage.setSrc(path);
        if(this.save(bannerImage)){
            ossService.uploadPublicFile(file,path);
            return src;
        }else{
            throw new MyException("上传banner图片失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "banner",key = "'list'")
    public List<String> getBannerImageList() {
        List<BannerImage> bannerImages = this.list(new QueryWrapper<BannerImage>().select("src"));
        return bannerImages.stream().map(BannerImage::getSrc).collect(Collectors.toList());
    }

    @Override
    public String getRandomBannerImage() {
        List<String> banners = bannerImageService.getBannerImageList();
        int randomIndex = (int) (Math.random() * banners.size());
        return OssProperties.RESOURCE_DOMAIN + "/"+ banners.get(randomIndex);
    }
}
