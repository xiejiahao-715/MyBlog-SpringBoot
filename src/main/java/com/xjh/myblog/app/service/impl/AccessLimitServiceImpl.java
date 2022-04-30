package com.xjh.myblog.app.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xjh.myblog.app.annotation.AccessLimit;
import com.xjh.myblog.app.cacheservice.AccessLimitCacheService;
import com.xjh.myblog.app.service.AccessLimitService;
import com.xjh.myblog.common.api.ResultCode;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.common.utils.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Service
public class AccessLimitServiceImpl implements AccessLimitService {

    @Autowired
    private AccessLimitCacheService accessLimitCacheService;

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Override
    public void doAccessLimit(AccessLimit accessLimitConfig) {
        String ip = ServletUtil.getRequestIpAddr();
        Boolean checkResult = accessLimitCacheService.checkAccess(ip, accessLimitConfig);
        if (checkResult == null) {
            log.error("redis服务异常，请联系管理员修复");
            return;
        }
        if (!checkResult) {
            throw new MyException(ResultCode.LIMIT_IP_REQUEST.getCode(), "检测到此ip: " + ip + " 恶意访问本站接口,以拒绝访问");
        }

    }

    @Override
    public String getUnAccessLimitVerifyCode() {
        // 获取ip地址
        String ip = ServletUtil.getRequestIpAddr();
        Boolean isBlackIp = accessLimitCacheService.isBlackIp(ip);
        if (isBlackIp != null && !isBlackIp) {
            throw new MyException("无法获取验证码，该IP访问正常");
        }else if(isBlackIp == null){
            throw new MyException("redis服务异常，请联系管理员修复");
        }
        String code = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(code);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 写入流中
            ImageIO.write(image, "jpeg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            // 将验证码存入redis
            accessLimitCacheService.setVerifyCode(ip,code);
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            throw new MyException(ResultCode.SYSTEM_ERROR.getCode(), "获取验证码失败");
        }
    }

    @Override
    public boolean verifyUnAccessLimitCode(String code) {
        // 获取ip地址
        String ip = ServletUtil.getRequestIpAddr();
        Boolean isBlackIp = accessLimitCacheService.isBlackIp(ip);
        if (isBlackIp != null && !isBlackIp) {
            throw new MyException("该IP访问正常，调用此接口无效");
        }else if(isBlackIp == null){
            throw new MyException("redis服务异常，请联系管理员修复");
        }
        String realCode = accessLimitCacheService.getVerifyCode(ip);
        if (realCode == null) {
            throw new MyException("请先获取验证码,或验证码已过期");
        }
        if (Objects.equals(realCode, code)) {
            // 验证码正确，将ip从黑名单中移除，同时删除验证阿
            accessLimitCacheService.enableIpAccess(ip);
            accessLimitCacheService.delVerifyCode(ip);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void interceptBlackIp() {
        // 获取ip地址
        String ip = ServletUtil.getRequestIpAddr();
        Boolean isBlackIp = accessLimitCacheService.isBlackIp(ip);
        if(isBlackIp != null && isBlackIp){
            throw new MyException(ResultCode.LIMIT_IP_REQUEST.getCode(), "检测到此ip: " + ip + " 恶意访问本站接口,以拒绝访问");
        }
    }
}
