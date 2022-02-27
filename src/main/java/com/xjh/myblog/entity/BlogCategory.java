package com.xjh.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "blog_category")
public class BlogCategory {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String title;
    private String href;

}
