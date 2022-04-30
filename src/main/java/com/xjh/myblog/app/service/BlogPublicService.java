package com.xjh.myblog.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.app.entity.Blog;
import com.xjh.myblog.app.entity.pojo.BlogYearArchive;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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

    /**
     * 获得文件的归档 按照 年-月-日 进行归档
     * @return 返回的是一个利用两层Map组织成的树形结构，利用 年-月 找到xx年xx月下的博客，
     * 且Map对象都是用的TreeMap来保证存储时都已经按照时间大小排序了，同理List<Blog>也排序好
     */
    Map<Integer,Map<Integer,List<Blog>>> getBlogArchiveTree();

    /**
     * 获取一个归档对象
     * @return 把树形结构的归档对象转换为了列表结构
     */
    List<BlogYearArchive> getBlogArchiveList();
}
