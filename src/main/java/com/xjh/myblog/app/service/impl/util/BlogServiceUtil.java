package com.xjh.myblog.app.service.impl.util;

import com.xjh.myblog.app.utils.OssPropertiesUtil;
import com.xjh.myblog.app.entity.Blog;
import com.xjh.myblog.app.entity.vo.BlogVo;
import com.xjh.myblog.common.utils.StringUtil;

import java.util.List;

// 有关 博客 操作的共有代码
public class BlogServiceUtil {
    // 为封面图片cover生成超链接
    public static String generateBlogCoverLink(String cover){
        if(StringUtil.notNull(cover)){
            return OssPropertiesUtil.RESOURCE_DOMAIN + "/" + cover;
        }else{
            return null;
        }
    }
    // 为Blog对象的封面图片生成超链接
    public static void generateBlogCoverLink(Blog blog){
        if(blog != null){
           blog.setCover(generateBlogCoverLink(blog.getCover()));
        }
    }
    // 为BlogVo对象的封面图片生成超链接
    public static void generateBlogVoCoverLink(BlogVo blogVo){
        if(blogVo != null){
            blogVo.setCover(generateBlogCoverLink(blogVo.getCover()));
        }
    }

    // 生成List<Blog>结构中的封面图片的路径
    public static List<Blog> generateBlogListCoverLink(List<Blog> blogs){
        if(blogs !=null){
            blogs.stream().forEach(BlogServiceUtil::generateBlogCoverLink);
        }
        return blogs;
    }
    // 根据博客id和博客唯一标识符blogId生成出博客内容在OSS中存储的路径
    public static String generateBlogContentOssPath(Long id,String blogId){
        return "blog/" + id + "/" + blogId + ".md";
    }
}
