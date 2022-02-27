package com.xjh.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@TableName(value = "blog")
public class Blog {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    // 存储阿里云博客文件的名称(UUID)
    @JsonIgnore
    private String blogId;
    private String title;
    private String cover;
    // 所属分类的id
    private Long category;
    private Date publishTime;
    private String summary;
    // 文章是否被置顶
    private Boolean isTop;
    // 文章是否热度最高
    private Boolean isHot;
    // 浏览数
    private BigInteger viewCount;
    // 评论数
    private BigInteger commentCount;

    // 博客的状态
    private String status;
}
