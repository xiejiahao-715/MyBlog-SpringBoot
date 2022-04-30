package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.utils.OssPropertiesUtil;
import com.xjh.myblog.app.entity.BannerImage;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.mapper.BannerImageMapper;
import com.xjh.myblog.app.service.BannerImageService;
import com.xjh.myblog.app.service.OssService;
import com.xjh.myblog.common.utils.FileUtil;
import com.xjh.myblog.common.utils.StringUtil;
import com.xjh.myblog.common.utils.UUIDUtil;
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
        String src = OssPropertiesUtil.RESOURCE_DOMAIN+"/"+path;
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
    public List<BannerImage> getBannerImageList() {
        return this.list().stream().filter(bannerImage -> {
            if(StringUtil.notNull(bannerImage.getSrc())){
                bannerImage.setSrc(OssPropertiesUtil.RESOURCE_DOMAIN + "/" + bannerImage.getSrc());
                return true;
            }else{
                return false;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String getRandomBannerImage() {
        List<BannerImage> banners = bannerImageService.getBannerImageList();
        int randomIndex = (int) (Math.random() * banners.size());
        return banners.get(randomIndex).getSrc();
    }

    @Override
    @Transactional
    @CacheEvict(value = "banner",key = "'list'")
    public boolean deleteBannerImage(Long id) {
        // 这里就不对OSS中的文件进行删除了
        return this.removeById(id);
    }
}
