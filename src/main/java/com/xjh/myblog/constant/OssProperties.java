package com.xjh.myblog.constant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssProperties implements InitializingBean {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    @Value("${aliyun.oss.fullDomain}")
    private String fullDomain;
    @Value("${aliyun.oss.useNginxProxy}")
    private Boolean useNginxProxy;
    @Value("${aliyun.oss.proxyDomain}")
    private String proxyDomain;

    // 定义公开静态变量
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;
    // 访问资源所用到的域名
    public static String RESOURCE_DOMAIN;
    public static String FULL_DOMAIN;
    public static String PROXY_DOMAIN;

    @Override
    public void afterPropertiesSet(){
        END_POINT = this.endpoint;
        ACCESS_KEY_ID = this.accessKeyId;
        ACCESS_KEY_SECRET = this.accessKeySecret;
        BUCKET_NAME = this.bucketName;
        FULL_DOMAIN = this.fullDomain;
        PROXY_DOMAIN = this.proxyDomain;
        RESOURCE_DOMAIN = this.useNginxProxy ? this.proxyDomain : this.fullDomain;
    }
}
