package com.xjh.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xjh.myblog.utils.IgnoreEmptyStringDeserializer;
import lombok.Data;

@Data
@TableName(value = "admin")
public class Admin {

    @TableId(value = "uid",type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // 只参与序列化
    private Long uid;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    private String username;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    private String password;
}
