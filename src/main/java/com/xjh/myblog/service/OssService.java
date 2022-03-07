package com.xjh.myblog.service;

import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface OssService {
    /**
     * 从阿里云OSS上下载一个文件 并直接将内容写入到相应对象response中
     * @param filePath 文件在OSS中的完整路径  例如下载文件路径为 blog/1/test.md
     * @param filename  下载的文件名称
     * @param suffix  下载文件的后缀名
     */
    void downloadFile(HttpServletResponse response,String filePath,String filename,String suffix);

    /**
     *  通过md文档内容解析出图片的存储位置，并加入到zip文件中，最终zip文件包含md文档和所有图片
     * @param response 代表响应的response对象
     * @param filePath 博客md文档的位置
     * @param filename zip文件的内容
     * @param suffix 后缀名
     */
    void downloadBlogZip(HttpServletResponse response,String filePath,String filename,String suffix);

    /**
     * 上传一个文件到Oss 查看是否成功
     * @param file 上传的文件
     * @param filePath 上传文件的路径
     * @param metadata 设置存储类型和访问权限
     * @return 是否成功
     */
    boolean uploadFile(MultipartFile file, String filePath, ObjectMetadata metadata);
    // 文件权限为 公共读
    boolean uploadPublicFile(MultipartFile file,String filePath);
    // 文件权限为 私有
    boolean uploadPrivateFile(MultipartFile file,String filePath);

    /**
     * 查询一个文件是否存在
     * @param path 查询文件的完整路径
     * @return 是否存在
     */
    boolean isFileExist(String path);

    /**
     * 永久删除一个文件
     * @param path 文件在OSS中的路径
     * @return 是否删除成功
     */
    boolean deleteFile(String path);
}
