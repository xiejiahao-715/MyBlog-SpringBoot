package com.xjh.myblog.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.app.entity.Social;

import java.util.List;

public interface SocialService extends IService<Social> {
    /**
     * @return 获取社交信息的列表
     */
    List<Social> getSocials();
}
