package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.constant.ENUM.BlogStatus;
import com.xjh.myblog.constant.OssProperties;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.entity.vo.BlogVo;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.BlogMapper;
import com.xjh.myblog.service.BlogCategoryService;
import com.xjh.myblog.service.BlogService;
import com.xjh.myblog.service.OssService;
import com.xjh.myblog.utils.FileUtil;
import com.xjh.myblog.utils.StringUtil;
import com.xjh.myblog.utils.UUIDUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private BlogCategoryService blogCategoryService;
    @Autowired
    private OssService ossService;

    @Override
    @Transactional
    public Long createBlog(BlogVo blogVo) {
        if(blogVo.getTitle() == null){
            throw new MyException("博客标题不能为空");
        }
        if(!blogCategoryService.isCategoryIdExist(blogVo.getCategory())){
            throw new MyException("分类不存在");
        }
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVo,blog);
        blog.setId(null);
        blog.setBlogId(UUIDUtil.getUUID32());
        blog.setStatus(BlogStatus.TEMP.getBlogStatus());
        if(this.save(blog)){
            return blog.getId();
        }else{
            throw new MyException("服务器出错");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BlogVo getBlogBasicInfoById(Long id) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.select("title","cover","category","summary").eq("id",id);
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取博客基本信息(该博客id不存在)");
        }
        BlogVo blogVo = new BlogVo();
        BeanUtils.copyProperties(blog,blogVo);
        // 拼接博客封面图片的url
        if(StringUtil.notNull(blogVo.getCover())){
            blogVo.setCover(OssProperties.RESOURCE_DOMAIN +"/"+ blogVo.getCover());
        }
        return blogVo;
    }

    @Override
    @Transactional
    public boolean updateBlogBasicInfo(BlogVo blogVo) {
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVo,blog);
        if(baseMapper.updateById(blog) != 1){
            throw new MyException("修改博客信息失败");
        }else return true;
    }

    @Override
    @Transactional
    public String uploadBlogCoverImage(Long id, MultipartFile imageFile) {
        // 判断该id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传博客封面(该博客id不存在)");
        }
        // 生成在OOS中的存储路径
        String suffix = FileUtil.getSuffix(imageFile);
        // 所有的博客封面图片名都为coverImage
        String fileName = "coverImage";
        String relativePath = "blog/"+id+"/"+ fileName+suffix;
        String imageUrl = OssProperties.RESOURCE_DOMAIN +"/"+relativePath;
        // 封面图片的路径更新到数据库
        blog = new Blog();
        blog.setId(id);
        // 封面路径只需要保存在oss中的路径不需要域名
        blog.setCover(relativePath);
        this.saveOrUpdate(blog);
        if(ossService.uploadPublicFile(imageFile,relativePath)){
            return imageUrl;
        }else{
            throw new MyException("上传失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String uploadImage(Long id, MultipartFile file) {
        // 判断该id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传图片(博客id不存在)");
        }
        // 生成在OOS中的存储路径
        String suffix = FileUtil.getSuffix(file);
        String filename = UUIDUtil.getUUID32();
        String relativePath = "blog/"+id+"/"+ filename +suffix;
        String url = OssProperties.RESOURCE_DOMAIN +"/"+relativePath;
        if(ossService.uploadPublicFile(file,relativePath)){
            return url;
        }else {
            throw new MyException("上传失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean uploadBlogContent(Long id, MultipartFile file) {
        // 判断该id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传博客内容该(博客id不存在)");
        }
        // 生成在OOS中的存储路径  将内容存储为.md格式
        String relativePath = generateBlogOssPath(id,blog.getBlogId());
        if(ossService.uploadPrivateFile(file,relativePath)){
            return true;
        }else{
            throw new MyException("上传博客内容失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getBlogContent(Long id, HttpServletResponse response) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("获取博客内容失败(博客id不存在)");
        }
        // 得到博客在oos中存储的位置
        String filename = blog.getTitle();
        String relativePath =  generateBlogOssPath(id,blog.getBlogId());
        ossService.downloadFile(response,relativePath,filename,".md");
    }

    @Override
    @Transactional(readOnly = true)
    public void downloadBlogZip(Long id, HttpServletResponse response) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("下载博客失败(博客id不存在)");
        }
        // 得到博客在oos中存储的位置
        String filename = blog.getTitle();
        String relativePath = generateBlogOssPath(id,blog.getBlogId());
        ossService.downloadBlogZip(response,relativePath,filename,".zip");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> getBlogPageAllStatus(Long current,Long limit) {
        Page<Blog> blogIPage = new Page<>(current,limit);
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        // 执行查询
        this.page(blogIPage,wrapper);
        // 修改封面图片路径
        blogIPage.setRecords(generateBlogsCoverPath(blogIPage.getRecords()));
        return blogIPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> getPublishedBlogPage(Long current, Long limit,Long categoryId) {
        if(categoryId != null && !blogCategoryService.isCategoryIdExist(categoryId)){
            throw new MyException("获取发布博客分页信息：博客分类id不存在");
        }
        Page<Blog> blogIPage = new Page<>(current,limit);
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        if(categoryId != null){
            wrapper.eq("category",categoryId);
        }
        wrapper.eq("status",BlogStatus.PUBLISHED);
        wrapper.orderByDesc("publish_time");
        // 执行查询
        this.page(blogIPage,wrapper);
        // 修改封面图片路径
        blogIPage.setRecords(generateBlogsCoverPath(blogIPage.getRecords()));
        return blogIPage;
    }

    @Override
    @Transactional
    public boolean publishBlog(Long id) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("发布博客失败(博客id不存在)");
        }
        // 博客状态必须是 为保存状态
        if(!BlogStatus.TEMP.getBlogStatus().equals(blog.getStatus())){
            throw new MyException("发布博客失败(博客状态必须是未保存状态)");
        }
        String relativePath = generateBlogOssPath(id,blog.getBlogId());
        // 判断博客文件是否在OSS存在
        if(!ossService.isFileExist(relativePath)){
            throw new MyException("发布博客失败(博客文件不存在)");
        }
        // 修改数据库
        blog.setPublishTime(new Date());
        blog.setStatus(BlogStatus.PUBLISHED.getBlogStatus());
        if(this.updateById(blog)){
            return true;
        }else{
            throw new MyException("发布博客失败(修改异常,请稍后再试)");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Blog getPublishedBlogById(Long id) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id).eq("status",BlogStatus.PUBLISHED.getBlogStatus());
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取已发布博客信息失败");
        }
        if(StringUtil.notNull(blog.getCover())){
            blog.setCover(OssProperties.RESOURCE_DOMAIN+"/"+blog.getCover());
        }
        return blog;
    }

    @Override
    public void getPublishedBlogContentById(Long id, HttpServletResponse response) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id).eq("status",BlogStatus.PUBLISHED.getBlogStatus());
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取博客内容失败(id不存在)");
        }
        // 得到博客在oos中存储的位置
        String filename = blog.getTitle();
        String relativePath =  generateBlogOssPath(id,blog.getBlogId());
        ossService.downloadFile(response,relativePath,filename,".md");
    }


    // 生成blog文件在阿里云oss中存储的位置
    private String generateBlogOssPath(Long id, String filename){
        return "blog/" + id + "/" + filename + ".md";
    }
    // 生成List<Blog>结构中的封面图片的路径
    private List<Blog> generateBlogsCoverPath(List<Blog> blogs){
        if(blogs!=null){
            blogs.stream().forEach(blog -> {
                if(StringUtil.notNull(blog.getCover())){
                    blog.setCover(OssProperties.RESOURCE_DOMAIN+"/"+blog.getCover());
                }
            });
        }
        return blogs;
    }
}
