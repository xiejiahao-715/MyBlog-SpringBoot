package com.xjh.myblog.entity.vo;

import lombok.Data;

// 用于修改网站基本信息的表单
@Data
public class WebsiteInfoVo {

    // 网站id
    private Long id;
    // 网站名
    private String name;
    // 个性签名
    private String slogan;
    // 通知信息
    private String notice;
    // 个人描述
    private String desc;
}
