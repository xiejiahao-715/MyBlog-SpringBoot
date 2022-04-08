package com.xjh.myblog.entity.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlogYearArchive {
    private int year;
    private List<BlogMonthArchive> monthArchives = new ArrayList<>();
}
