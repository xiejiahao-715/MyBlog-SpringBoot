package com.xjh.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xjh.myblog.utils.IgnoreEmptyStringDeserializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@TableName(value = "admin")
public class Admin {

    @TableId(value = "uid",type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // 只参与序列化
    private Long uid;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 只参与反序列化
    @NotBlank(message = "密码不能为空")
    private String password;
}
