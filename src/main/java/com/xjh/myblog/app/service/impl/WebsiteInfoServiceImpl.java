package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.entity.WebsiteInfo;
import com.xjh.myblog.app.entity.vo.WebsiteInfoVo;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.mapper.WebsiteInfoMapper;
import com.xjh.myblog.app.service.WebsiteInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
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

    @Override
    @CacheEvict(value = "websiteInfo",key = "#websiteInfoVo.getId()",condition = "#websiteInfoVo?.getId() != null")
    @Transactional
    public boolean updateWebsiteInfoById(WebsiteInfoVo websiteInfoVo) {
        WebsiteInfo websiteInfo = new WebsiteInfo();
        BeanUtils.copyProperties(websiteInfoVo,websiteInfo);
        if(baseMapper.updateById(websiteInfo) == 1){
            return true;
        }else{
            throw new MyException("修改网站信息:错误");
        }
    }
}
