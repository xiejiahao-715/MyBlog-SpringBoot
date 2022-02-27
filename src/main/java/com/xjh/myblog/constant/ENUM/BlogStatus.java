package com.xjh.myblog.constant.ENUM;

public enum BlogStatus {
    // 代表该博客是临时保存的
    TEMP("temp"),
    // 博客被发布
    PUBLISHED("published"),
    // 博客被删除
    DELETED("deleted");
    private final String blogStatus;
    BlogStatus(String status){
        this.blogStatus = status;
    }
    public String getBlogStatus(){
        return this.blogStatus;
    }
}
