package com.xjh.myblog.app.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// 用于修改网站基本信息的表单
@Data
public class WebsiteInfoVo {

    // 网站id
    @NotNull(message = "网站id不能为空")
    private Long id;
    // 网站名
    @NotBlank(message = "网站名不能为空")
    private String name;
    // 个性签名
    private String slogan;
    // 通知信息
    private String notice;
    // 个人描述
    private String desc;
}
