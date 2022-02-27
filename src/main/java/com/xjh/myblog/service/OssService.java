package com.xjh.myblog.service;

import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface OssService {
    /**
     * 从阿里云OSS上下载一个文件 并返回前端该文件的二进制数据
     * @param path 文件在OSS中的完整路径  例如下载文件路径为/blog/1/text.md
     * @param filename  文件名(下载时显示的文件名)
     * @param suffix  文件后缀名(下载时显示的后缀名)
     */
    void downloadFile(HttpServletResponse response,String path,String filename,String suffix);
    // 从阿里云下载一个博客文件  需要解析文件中的图片的路径  自动打包图片
    void downloadBlogZip(HttpServletResponse response,String path,String filename,String suffix);

    // 上传一个文件 查看是否成功 设置存储类型和访问权限
    boolean uploadFile(MultipartFile file, String path, ObjectMetadata metadata);
    boolean uploadPublicFile(MultipartFile file,String path);
    boolean uploadPrivateFile(MultipartFile file,String path);

    // 判断一个文件是否存在 path: 代表查询的文件名(已经包含了路径)
    boolean isFileExist(String path);


}
