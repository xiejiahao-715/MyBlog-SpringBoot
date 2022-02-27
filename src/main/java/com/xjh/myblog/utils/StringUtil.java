package com.xjh.myblog.utils;

public class StringUtil {
    public static boolean notNull(String s){
        return s != null &&  s.trim().length() > 0;
    }
}
