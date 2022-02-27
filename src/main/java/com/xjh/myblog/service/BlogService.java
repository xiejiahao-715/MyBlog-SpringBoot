package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.entity.vo.BlogVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface BlogService extends IService<Blog> {
    // 创建博客 返回博客的id
    Long createBlog(BlogVo blogVo);

    // 根据id获取博客的基本信息
    BlogVo getBlogBasicInfoById(Long id);
    // 更新博客的基本信息
    boolean updateBlogBasicInfo(BlogVo blogVo);

    // 上传博客的封面图片  返回图片的链接
    String uploadBlogCoverImage(Long id, MultipartFile imageFile);
    // 上传博客内容中的图片
    String uploadImage(Long id,MultipartFile file);
    // 上传博客的具体内容
    boolean uploadBlogContent(Long id,MultipartFile file);
    // 获取博客的内容  以文件流的形式返回  如果返回true代表成功 反之则不成功
    void getBlogContent(Long id, HttpServletResponse response);
    void downloadBlogZip(Long id, HttpServletResponse response);

    /**
     * 获取所有状态的博客
     * @param current  当前页
     * @param limit  每页的个数
     * @return  返回分页对象
     */
    Page<Blog> getBlogPageAllStatus(Long current, Long limit);


    // 发布博客(修改博客的状态为 发布)
    boolean publishBlog(Long id);

    // 获取已经发布博客的基本信息 不需要权限验证
    Blog getPublishedBlogById(Long id);
    Page<Blog> getPublishedBlogPage(Long current,Long limit,Long categoryId);
    void getPublishedBlogContentById(Long id,HttpServletResponse response);
}
