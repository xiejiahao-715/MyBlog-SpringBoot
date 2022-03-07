package com.xjh.myblog.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.xjh.myblog.constant.OssProperties;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.service.OssService;
import com.xjh.myblog.utils.UUIDUtil;
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
    // 创建OSSClient实例 记得调用shutdown()方法关闭连接
    private OSS createOssClient(){
        return new OSSClientBuilder()
                .build(OssProperties.END_POINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);
    }

    @Override
    public void downloadFile(HttpServletResponse response,String path,String filename,String suffix) {
        // 创建OSSClient实例。
        OSS ossClient = createOssClient();

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
        OSS ossClient = createOssClient();
        // 判断文件是否存在
        boolean found = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
        if(!found) throw new MyException("下载博客:文件不存在");
        OSSObject ossObject = ossClient.getObject(OssProperties.BUCKET_NAME, path);
        // 生成一个临时的.zip文件来保存博客
        File tempZipFile = null;
        try {
            tempZipFile = File.createTempFile(UUIDUtil.getUUID32(),".zip");
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("下载博客:服务器异常");
        }
        // 读取图片的流对象
        InputStream inputStream = null;
        // 读取文件的流对象，将zip文件写入到response返回给前端
        InputStream fileInputStream = null;
        try (BufferedReader blogReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
             ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempZipFile), StandardCharsets.UTF_8);
             OutputStream outputStream = response.getOutputStream()) {
            // 开始压缩md文档
            zipOutputStream.putNextEntry(new ZipEntry(filename + ".md"));
            // 从文档中解析需要用到的文件 将其在OSS中的路径存储下来 对于文本逐行解析
            HashSet<String> imageFilePaths = new HashSet<>();
            // 匹配img标签中的src属性 例如<img src="/resource/test.jpg" /> 匹配出test.jpg
            String imgTagRegex = String.format("(<img\\b.*?src=\")(%s|%s)/((.*?/)*(.*?))(\".*?>)", OssProperties.FULL_DOMAIN,OssProperties.PROXY_DOMAIN);
            // 匹配markdown语法中的图片的路径 例如 ![test.jpg](/resource/test.jpg) 匹配出test.jpg
            String imgMdRegex = String.format("(!\\[.*?\\])\\( *?(%s|%s)/((.*?/)*(.*?))\\)",OssProperties.FULL_DOMAIN,OssProperties.PROXY_DOMAIN);
            Pattern imgTagPattern = Pattern.compile(imgTagRegex);
            Pattern imgMdPattern = Pattern.compile(imgMdRegex);
            // 读取博客文件  并压缩
            while (true) {
                String line = blogReader.readLine();
                if (line == null) break;
                // 替换文档中<img>标签中src的值
                Matcher matcher = imgTagPattern.matcher(line);
                StringBuffer stringBuffer = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(stringBuffer, matcher.group(1) + matcher.group(5) + matcher.group(6));
                    imageFilePaths.add(matcher.group(3));
                }
                matcher.appendTail(stringBuffer);
                // 替换 ![]() 结构中图片路径
                line = stringBuffer.toString();
                stringBuffer.delete(0,stringBuffer.length());
                matcher = imgMdPattern.matcher(line);
                while (matcher.find()){
                    matcher.appendReplacement(stringBuffer,matcher.group(1) + "(" + matcher.group(5) + ")");
                    imageFilePaths.add(matcher.group(3));
                }
                matcher.appendTail(stringBuffer);
                // 到此图片解析完成，尾部加入换行符
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
                String imageName = imagePath.replaceFirst("(.*?/)*(.*?)", "$2");
                zipOutputStream.putNextEntry(new ZipEntry(imageName));
                while ((n = inputStream.read(bytes)) != -1){
                    zipOutputStream.write(bytes,0,n);
                }
                zipOutputStream.closeEntry();
                inputStream.close();
            }
            zipOutputStream.close();
            // 临时zip文件生成完毕，实现文件下载功能
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename + suffix, "utf-8"));
            response.setContentType("application/octet-stream");
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + tempZipFile.length());
            fileInputStream = new FileInputStream(tempZipFile);
            while ((n = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,n);
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
            try {
                if(inputStream != null){
                    inputStream.close();
                }
                if(fileInputStream != null){
                    fileInputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            } finally {
                // 当所有流关闭后删除临时文件
                tempZipFile.delete();
            }

        }
    }

    @Override
    public boolean uploadFile(MultipartFile file, String path,ObjectMetadata metadata) {
        // 创建OSSClient实例。
        OSS ossClient = createOssClient();

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
        OSS ossClient = createOssClient();
        boolean isExist = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
        ossClient.shutdown();
        return isExist;
    }

    @Override
    public boolean deleteFile(String path) {
        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = createOssClient();
            boolean found = ossClient.doesObjectExist(OssProperties.BUCKET_NAME,path);
            if(!found) throw new MyException("删除文件:文件不存在");
            ossClient.deleteObject(OssProperties.BUCKET_NAME,path);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally{
            if(ossClient != null){
                ossClient.shutdown();
            }
        }
        return true;
    }
}
