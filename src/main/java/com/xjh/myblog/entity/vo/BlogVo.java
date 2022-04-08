package com.xjh.myblog.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xjh.myblog.utils.IgnoreEmptyStringDeserializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


// 用于上传博客的表单对象
@Data
public class BlogVo {
    @JsonIgnore // 不参与序列化
    private Long id;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    @NotBlank(message = "博客标题不能为空")
    private String title;

    @NotNull(message = "博客分类不能为空")
    private Long category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // 只参与序列化
    private String cover;

    @JsonDeserialize(using = IgnoreEmptyStringDeserializer.class)
    private String summary;
}
