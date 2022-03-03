package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.constant.ENUM.BlogStatus;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.BlogMapper;
import com.xjh.myblog.service.BlogCategoryService;
import com.xjh.myblog.service.BlogPublicService;
import com.xjh.myblog.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import static com.xjh.myblog.service.impl.util.BlogServiceUtil.*;


@Service
public class BlogPublicServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogPublicService {

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private OssService ossService;

    @Autowired
    @Lazy // 避免 this 调用 AOP失效
    private BlogPublicService blogPublicService;

    @Override
    @Transactional(readOnly = true)
    public Blog getPublishedBlogById(Long id) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id).eq("status", BlogStatus.PUBLISHED.getBlogStatus());
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取已发布博客信息:博客id不存在");
        }
        // 处理图片封面路径
        generateBlogCoverLink(blog);
        return blog;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> getPublishedBlogPage(Long current, Long limit,Long categoryId) {
        if(categoryId != null && !blogCategoryService.isCategoryIdExist(categoryId)){
            throw new MyException("获取发布博客分页信息:分类id不存在");
        }
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        if(categoryId != null){
            wrapper.eq("category",categoryId);
        }
        wrapper.eq("status",BlogStatus.PUBLISHED);
        // 根据发布时间降序
        wrapper.orderByDesc("publish_time");
        // 分页对象
        Page<Blog> blogIPage = new Page<>(current,limit);
        // 执行查询
        this.page(blogIPage,wrapper);
        // 修改封面图片路径
        blogIPage.setRecords(generateBlogListCoverLink(blogIPage.getRecords()));
        return blogIPage;
    }
    @Override
    public Page<Blog> getPublishedBlogPage(Long current, Long limit) {
        return blogPublicService.getPublishedBlogPage(current,limit,null);
    }

    @Override
    @Transactional(readOnly = true)
    public void getPublishedBlogContentById(Long id, HttpServletResponse response) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.select("blog_id","title").eq("id",id).eq("status",BlogStatus.PUBLISHED.getBlogStatus());
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取已发布博客的具体内容:博客id不存在");
        }
        // 得到博客在oos中存储的位置
        String filename = blog.getTitle();
        String filePath =  generateBlogContentOssPath(id,blog.getBlogId());
        ossService.downloadFile(response,filePath,filename,".md");
    }

    @Override
    @Transactional(readOnly = true)
    public void downloadBlogZip(Long id, HttpServletResponse response) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("下载博客:博客id不存在");
        }
        String filename = blog.getTitle();
        String relativePath = generateBlogContentOssPath(id,blog.getBlogId());
        ossService.downloadBlogZip(response,relativePath,filename,".zip");
    }
}
