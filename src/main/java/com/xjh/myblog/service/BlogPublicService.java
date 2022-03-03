package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Blog;

import javax.servlet.http.HttpServletResponse;

// 获取博客信息的接口,不需要权限
public interface BlogPublicService extends IService<Blog> {
    /**
     * 根据博客id获取已发布博客的信息
     * @param id 博客id
     * @return 返回博客对象
     */
    Blog getPublishedBlogById(Long id);

    /**
     * 以分页方式获取已发布博客的分页对象
     * @param current 当前的页码
     * @param limit 每一页记录的数量
     * @param categoryId 获取博客的分类，为null或缺省则 代表获取全部分类
     * @return 分页对象，包含记录，总记录数等等
     */
    Page<Blog> getPublishedBlogPage(Long current, Long limit, Long categoryId);
    Page<Blog> getPublishedBlogPage(Long current, Long limit);

    /**
     * 获取已发布博客的具体内容，将内容以流的形式写入到 代表响应的response对象
     * @param id 博客的id
     * @param response 代表响应的response对象
     */
    void getPublishedBlogContentById(Long id, HttpServletResponse response);

    /**
     * 下载博客 为zip压缩文件，通过md文档内容解析出图片的存储位置，并加入到zip文件中，最终zip文件包含md文档和所有图片
     * @param id 博客id
     * @param response 代表响应的response对象
     */
    void downloadBlogZip(Long id, HttpServletResponse response);
}
