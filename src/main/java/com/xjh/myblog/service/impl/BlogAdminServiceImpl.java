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
import com.xjh.myblog.service.BlogAdminService;
import com.xjh.myblog.service.BlogCategoryService;
import com.xjh.myblog.service.OssService;
import com.xjh.myblog.utils.FileUtil;
import com.xjh.myblog.utils.UUIDUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static com.xjh.myblog.service.impl.util.BlogServiceUtil.*;


@Service
public class BlogAdminServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogAdminService {

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private OssService ossService;

    @Override
    @Transactional
    public Long createBlog(BlogVo blogVo) {
        if(blogVo.getTitle() == null){
            throw new MyException("创建博客:博客标题不能为空");
        }
        if(!blogCategoryService.isCategoryIdExist(blogVo.getCategory())){
            throw new MyException("创建博客:分类id不存在");
        }
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVo,blog);
        blog.setId(null);
        blog.setBlogId(UUIDUtil.getUUID32());
        blog.setStatus(BlogStatus.TEMP.getBlogStatus());
        if(this.save(blog)){
            return blog.getId();
        }else{
            throw new MyException("创建博客:未知错误");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BlogVo getBlogBasicInfoById(Long id) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.select("title","cover","category","summary").eq("id",id);
        Blog blog = this.getOne(wrapper);
        if(blog == null){
            throw new MyException("获取博客基本信息:博客id不存在");
        }
        BlogVo blogVo = new BlogVo();
        BeanUtils.copyProperties(blog,blogVo);
        // 设置封面图片的超链接
        generateBlogVoCoverLink(blogVo);
        return blogVo;
    }

    @Override
    @Transactional
    public boolean updateBlogBasicInfo(BlogVo blogVo) {
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVo,blog);
        if(baseMapper.updateById(blog) != 1){
            throw new MyException("修改博客基本信息:失败");
        }else {
            return true;
        }
    }

    @Override
    @Transactional
    public String uploadBlogCoverImage(Long id, MultipartFile imageFile) {
        // 判断博客id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传博客封面:博客id不存在");
        }
        // 生成在OOS中的存储路径
        String suffix = FileUtil.getSuffix(imageFile);
        // 所有的博客封面图片名都为coverImage
        String imageName = "coverImage";
        String imagePath= "blog/"+ id + "/" + imageName + suffix;
        // 封面图片的路径更新到数据库
        blog = new Blog();
        blog.setId(id);
        // 封面路径只需要保存在oss中的路径不需要完整的路径连接
        blog.setCover(imagePath);
        // 存入数据库
        this.saveOrUpdate(blog);
        if(ossService.uploadPublicFile(imageFile,imagePath)){
            return generateBlogCoverLink(imagePath);
        }else{
            throw new MyException("上传博客封面:失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String uploadImage(Long id, MultipartFile imageFile) {
        // 判断该id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传图片:博客id不存在");
        }
        // 生成在OOS中的存储路径
        String suffix = FileUtil.getSuffix(imageFile);
        String filename = UUIDUtil.getUUID32();
        String filePath = "blog/" + id + "/" + filename + suffix;
        String url = OssProperties.RESOURCE_DOMAIN + "/" + filePath;
        if(ossService.uploadPublicFile(imageFile,filePath)){
            return url;
        }else {
            throw new MyException("上传图片:失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean uploadBlogContent(Long id, MultipartFile file) {
        // 判断该id是否存在
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("上传博客内容:博客id不存在");
        }
        // 生成在OOS中的存储路径  将内容存储为.md格式
        String filePath = generateBlogContentOssPath(id,blog.getBlogId());
        if(ossService.uploadPrivateFile(file,filePath)){
            return true;
        }else{
            throw new MyException("上传博客内容:失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getBlogContent(Long id, HttpServletResponse response) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("获取博客内容:博客id不存在");
        }
        // 得到博客在oos中存储的位置
        String filename = blog.getTitle();
        String filePath =  generateBlogContentOssPath(id,blog.getBlogId());
        ossService.downloadFile(response,filePath,filename,".md");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Blog> getBlogPageAllStatus(Long current, Long limit) {
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        // 更具id降序
        wrapper.orderByDesc("id");
        // 执行查询
        Page<Blog> blogIPage = new Page<>(current,limit);
        this.page(blogIPage,wrapper);
        // 修改封面图片路径
        blogIPage.setRecords(generateBlogListCoverLink(blogIPage.getRecords()));
        return blogIPage;
    }

    @Override
    @Transactional
    public boolean publishBlog(Long id) {
        Blog blog = this.getById(id);
        if(blog == null){
            throw new MyException("发布博客:博客id不存在");
        }
        // 博客状态必须是 为临时保存状态
        if(!BlogStatus.TEMP.getBlogStatus().equals(blog.getStatus())){
            throw new MyException("发布博客:博客状态必须是临时保存状态");
        }
        String blogPath = generateBlogContentOssPath(id,blog.getBlogId());
        // 判断博客文件是否在OSS存在
        if(!ossService.isFileExist(blogPath)){
            throw new MyException("发布博客:博客内容为空");
        }
        // 修改数据库
        blog.setPublishTime(new Date());
        blog.setStatus(BlogStatus.PUBLISHED.getBlogStatus());
        if(this.updateById(blog)){
            return true;
        }else{
            throw new MyException("发布博客:未知异常");
        }
    }
}
