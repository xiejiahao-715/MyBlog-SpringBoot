package com.xjh.myblog.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "social")
public class Social {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String title;
    private String icon;
    private String color;
    private String href;

}
