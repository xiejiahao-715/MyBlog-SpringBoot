package com.xjh.myblog.utils;

import java.util.UUID;

public class UUIDUtil {
    // 得到32位的uuid
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
