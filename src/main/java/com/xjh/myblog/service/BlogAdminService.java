package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Blog;
import com.xjh.myblog.entity.vo.BlogVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

// 管理博客的管理员接口，需要进行权限验证
public interface BlogAdminService extends IService<Blog> {
    /**
     * 用于创建一个博客，返回创建博客的id,此时博客状态为：临时保存
     * @param blogVo 前端传递的blogVo对象
     * @return 返回新博客的id
     */
    Long createBlog(BlogVo blogVo);

    /**
     * 根据博客id获取博客的基本信息(博客可以处于任何状态:已发布，删除，未发布等)
     * @param id 博客id
     * @return 返回一个BlogVo对象
     */
    BlogVo getBlogBasicInfoById(Long id);

    /**
     * 修改博客的基本信息
     * @param blogVo 前端传过来的表单blogVo对象，依据此修改
     * @return 返回是否更新成功
     */
    boolean updateBlogBasicInfo(BlogVo blogVo);

    /**
     * 上传博客的封面图片
     * @param id 博客的id
     * @param imageFile 上传的图片文件
     * @return 返回图片的链接
     */
    String uploadBlogCoverImage(Long id, MultipartFile imageFile);

    /**
     * 上传博客内容中的图片
     * @param id 博客id
     * @param imageFile 图片文件
     * @return 返回图片的连接
     */
    String uploadImage(Long id,MultipartFile imageFile);

    /**
     * 上传博客的具体内容
     * @param id 博客id
     * @param file 包含博客内容的文件
     * @return 是否成功
     */
    boolean uploadBlogContent(Long id,MultipartFile file);

    /**
     * 获取博客的内容  以文件流的形式返回(博客可以处于任何状态:已发布，删除，未发布等)
     * @param id 博客id
     * @param response 响应体对象
     */
    void getBlogContent(Long id, HttpServletResponse response);

    /**
     * 获取所有状态的博客
     * @param current  当前的页码
     * @param limit  每页的个数
     * @return  返回分页对象
     */
    Page<Blog> getBlogPageAllStatus(Long current, Long limit);

    /**
     * 发布博客，修改博客的状态：临时保存->发布
     * @param id 博客id
     * @return 是否发布成功
     */
    boolean publishBlog(Long id);

    /**
     * 取消博客的发布，修改博客的状态为：发布->临时保存
     *
     */
    boolean cancelPublishBlog(Long id);
}
