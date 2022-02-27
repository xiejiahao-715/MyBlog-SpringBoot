package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.entity.WebsiteInfo;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.WebsiteInfoMapper;
import com.xjh.myblog.service.WebsiteInfoService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebsiteInfoServiceImpl extends ServiceImpl<WebsiteInfoMapper, WebsiteInfo> implements WebsiteInfoService {
    @Override
    @Cacheable(value = "websiteInfo",key = "#id")
    @Transactional(readOnly = true)
    public WebsiteInfo getWebsiteInfoById(Integer id) {
        WebsiteInfo websiteInfo = this.getById(id);
        if(websiteInfo == null){
            throw new MyException("网站id不存在");
        }
        return websiteInfo;
    }
}
