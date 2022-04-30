package com.xjh.myblog.app.entity.pojo;

import com.xjh.myblog.app.entity.Blog;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlogMonthArchive {
    private int month;
    private List<Blog> blogs = new ArrayList<>();

}
