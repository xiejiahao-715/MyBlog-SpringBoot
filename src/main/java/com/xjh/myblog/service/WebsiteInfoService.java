package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.WebsiteInfo;

public interface WebsiteInfoService extends IService<WebsiteInfo> {
    WebsiteInfo getWebsiteInfoById(Integer id);
}
