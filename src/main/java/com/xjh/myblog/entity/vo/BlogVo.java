package com.xjh.myblog.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xjh.myblog.utils.IgnoreEmptyStringDeserializer;
import lombok.Data;


// 用于上传博客的表单对象
@Data
public class BlogVo {
    @JsonIgnore // 不参与序列化
    private Long id;
    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    private String title;
    private Long category;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // 只参与序列化
    private String cover;
    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    private String summary;
}
