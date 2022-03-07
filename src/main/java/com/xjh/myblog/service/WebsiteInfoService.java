package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.WebsiteInfo;
import com.xjh.myblog.entity.vo.WebsiteInfoVo;

public interface WebsiteInfoService extends IService<WebsiteInfo> {
    /**
     * 根据id获取网站信息的介绍
     * @param id 网站的信息
     * @return 网站信息对象
     */
    WebsiteInfo getWebsiteInfoById(Integer id);

    /**
     * 根据前端表单来修改信息
     * @param websiteInfoVo 表单对象
     * @return 是否修改成功
     */
    boolean updateWebsiteInfoById(WebsiteInfoVo websiteInfoVo);
}
