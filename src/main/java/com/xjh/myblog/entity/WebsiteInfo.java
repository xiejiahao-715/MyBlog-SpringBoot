package com.xjh.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "website_info")
public class WebsiteInfo {
    @TableId(value = "id",type = IdType.AUTO)
    @JsonIgnore
    private Long id;

    private String name;
    private String avatar;
    private String domain;
    private String slogan;
    private String notice;
    @TableField("`desc`")
    private String desc;

    private Date createdTime;
}
