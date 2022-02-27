package com.xjh.myblog.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.xjh.myblog.constant.OssProperties;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.service.OssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public void downloadFile(HttpServletResponse response,String path,String filename,String suffix) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(OssProperties.END_POINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);

        // 判断文件是否存在
        boolean found = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
        if(!found) throw new MyException("获取博客内容(文件不存在)");
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(OssProperties.BUCKET_NAME, path);
        try(OutputStream outputStream =response.getOutputStream();
            InputStream inputStream = ossObject.getObjectContent()) {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename+suffix,"utf-8"));
            response.setContentType("application/octet-stream");
            byte[] buffer = new byte[1024];
            int n = 0;
            while((n = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void downloadBlogZip(HttpServletResponse response, String path, String filename,String suffix) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(OssProperties.END_POINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);
        // 判断文件是否存在
        boolean found = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
        if(!found) throw new MyException("下载博客(文件不存在)");
        OSSObject ossObject = ossClient.getObject(OssProperties.BUCKET_NAME, path);

        InputStream inputStream = null;
        try (BufferedReader blogReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
             ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename + suffix, "utf-8"));
            response.setContentType("application/octet-stream");
            // 开始压缩md文档
            zipOutputStream.putNextEntry(new ZipEntry(filename + ".md"));
            // 从文档中解析需要用到的文件 将其在OSS中的路径存储下来 对于文本逐行解析
            HashSet<String> imageFilePaths = new HashSet<>();
            // 匹配img标签中的src属性
            String regex = String.format("(<img\\b.*?src=\")(%s|%s)/(.*?)(\".*?>)", OssProperties.FULL_DOMAIN,OssProperties.PROXY_DOMAIN);
            Pattern pattern = Pattern.compile(regex);
            // 读取博客文件  并压缩
            while (true) {
                String line = blogReader.readLine();
                if (line == null) break;
                Matcher matcher = pattern.matcher(line);
                StringBuffer stringBuffer = new StringBuffer();
                while (matcher.find()) {
                    // 将匹配到的路径记录下来  路径格式为 blog/1/b64f69d38713447ab51cf99692afce04.png
                    String fileOssPath = matcher.group(3).trim();
                    String imageName = fileOssPath.replaceFirst("(.*/)?(.*)", "$2");
                    matcher.appendReplacement(stringBuffer, matcher.group(1) + imageName + matcher.group(4));
                    imageFilePaths.add(fileOssPath);
                }
                matcher.appendTail(stringBuffer);
                stringBuffer.append('\n');
                // 将解析的内容写入到压缩流
                zipOutputStream.write(stringBuffer.toString().getBytes(StandardCharsets.UTF_8));
            }
            // 压缩md文档完成
            zipOutputStream.closeEntry();
            blogReader.close();
            // 开始压缩携带的图片
            byte[] bytes = new byte[1024];
            int n = 0;
            for(String imagePath :imageFilePaths){
                OSSObject object = ossClient.getObject(OssProperties.BUCKET_NAME,imagePath);
                inputStream = object.getObjectContent();
                String imageName = imagePath.replaceFirst("(.*/)?(.*)", "$2");
                zipOutputStream.putNextEntry(new ZipEntry(imageName));
                while ((n = inputStream.read(bytes)) != -1){
                    zipOutputStream.write(bytes,0,n);
                }
                zipOutputStream.closeEntry();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean uploadFile(MultipartFile file, String path,ObjectMetadata metadata) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(OssProperties.END_POINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(OssProperties.BUCKET_NAME,path,inputStream);
            putObjectRequest.setMetadata(metadata);
            ossClient.putObject(putObjectRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ossClient.shutdown();
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }
    @Override
    public boolean uploadPublicFile(MultipartFile file,String path){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
        return this.uploadFile(file,path,objectMetadata);
    }

    @Override
    public boolean uploadPrivateFile(MultipartFile file, String path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectAcl(CannedAccessControlList.Private);
        return this.uploadFile(file,path,objectMetadata);
    }

    @Override
    public boolean isFileExist(String path) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(OssProperties.END_POINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);
        boolean isExist = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
        ossClient.shutdown();
        return isExist;
    }
}
