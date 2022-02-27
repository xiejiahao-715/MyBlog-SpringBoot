package com.xjh.myblog.utils;

import com.xjh.myblog.exceptionhandler.MyException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    // 获取文件后缀名 例如:1.png 返回.png
    public static String getSuffix(MultipartFile file){
        String filename = file.getOriginalFilename();
        if(filename == null) throw new MyException("文件异常");
        return filename.replaceAll("^.*(\\..*)$","$1");
    }
}
