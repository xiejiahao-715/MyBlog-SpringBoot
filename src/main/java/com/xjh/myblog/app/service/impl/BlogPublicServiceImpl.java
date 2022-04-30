package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.ENUM.BlogStatus;
import com.xjh.myblog.app.entity.Blog;
import com.xjh.myblog.app.entity.pojo.BlogMonthArchive;
import com.xjh.myblog.app.entity.pojo.BlogYearArchive;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.mapper.BlogMapper;
import com.xjh.myblog.app.service.BlogCategoryService;
import com.xjh.myblog.app.service.BlogPublicService;
import com.xjh.myblog.app.service.BlogViewCountService;
import com.xjh.myblog.app.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xjh.myblog.app.service.impl.util.BlogServiceUtil.*;


@Service
public class BlogPublicServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogPublicService {

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private OssService ossService;

    @Autowired
    private BlogViewCountService blogViewCountService;

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
        // 设置文章访问量
        blogViewCountService.setBlogViewCounts(blog);
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

        blogIPage.getRecords().forEach(blog->{
            // 修改封面图片路径
            generateBlogCoverLink(blog);
            // 设置访问量
            blogViewCountService.setBlogViewCounts(blog);
        });
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

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Map<Integer, List<Blog>>> getBlogArchiveTree() {
        // 获取所有的博客列表
        List<Blog> blogList = this.list(new QueryWrapper<Blog>().eq("status",BlogStatus.PUBLISHED));
        // 便于获取日期的 年-月-日
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 利用stream流Api操作
        return blogList.stream()
                .collect(Collectors.groupingBy(
                // 先按照 年 来分组
                blog -> Integer.parseInt(simpleDateFormat.format(blog.getPublishTime()).split("-")[0]),
                // 按照 年 的大小倒序
                () -> new TreeMap<>((year1,year2)-> year2 - year1),
                Collectors.groupingBy(
                        // 继续 按照 月 来分组  嵌套分组
                        blog-> Integer.parseInt(simpleDateFormat.format(blog.getPublishTime()).split("-")[1])
                        // 按照 月的大小来降序
                        ,() -> new TreeMap<>((month1,month2)-> month2 - month1),
                        // 将搜集到 Blog列表利用TreeSet进行 排序
                        Collectors.collectingAndThen(
                                Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(Blog::getPublishTime).reversed())),
                                ArrayList::new
                        )))
        );
    }

    // 获取一个归档对象 由树形结构转换而来
    @Override
    public List<BlogYearArchive> getBlogArchiveList(){
        // 首相获取 归档 的树形结构
        Map<Integer, Map<Integer, List<Blog>>> archive = blogPublicService.getBlogArchiveTree();
        // 将Map形式的树形结构转为List形式，也就是BlogArchiveVo对象
        List<BlogYearArchive> archiveList = new ArrayList<>();
        for(Map.Entry<Integer,Map<Integer, List<Blog>>> yearEntry : archive.entrySet()){
            // 获取年份归档
            BlogYearArchive yearArchive = new BlogYearArchive();
            yearArchive.setYear(yearEntry.getKey());
            for(Map.Entry<Integer, List<Blog>> monthEntry : yearEntry.getValue().entrySet()){
                // 添加月份归档
                BlogMonthArchive monthArchive = new BlogMonthArchive();
                monthArchive.setMonth(monthEntry.getKey());
                monthArchive.getBlogs().addAll(monthEntry.getValue());
                yearArchive.getMonthArchives().add(monthArchive);
            }
            archiveList.add(yearArchive);
        }
        return archiveList;
    }
}
