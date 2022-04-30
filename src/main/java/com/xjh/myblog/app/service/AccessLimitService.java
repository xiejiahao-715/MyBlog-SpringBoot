package com.xjh.myblog.app.service;

import com.xjh.myblog.app.annotation.AccessLimit;

// 有关访问限制的服务 用于防止频繁刷接口
public interface AccessLimitService {
    /**
     * 尝试接受一个ip的地址并记录下来，如果超过了请求的频率则会将这个ip加入到黑名单中，并抛出异常
     * @param accessLimitConfig 参数类型为AccessLimit注解，定义了超过请求频率的规则
     */
    void doAccessLimit(AccessLimit accessLimitConfig);

    /**
     * 生成一个请求解封黑名单IP的验证码 返回图片的base64编码
     * @return 返回二维码图片的base64编码字符串
     */
    String getUnAccessLimitVerifyCode();

    /**
     * 验证 验证码是否正确
     * @return 返回验证码是否正确，正确则解决ip
     */
    boolean verifyUnAccessLimitCode(String code);

    /**
     * 拦截 黑名单ip 的访问
     */
    void interceptBlackIp();
}
