package com.xjh.myblog.aspect;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xjh.myblog.ENUM.IResultCode;
import com.xjh.myblog.annotation.AccessLimit;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.utils.AopUtil;
import com.xjh.myblog.utils.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(0)
public class AccessLimitAspect {

    // ip黑名单的redisKey的前缀
    public final static String ipBlacklistPrefix = "ipBlacklist-";
    // ip黑名单的过期时间，默认 1 天
    public final static long ipBlacklistTTL = 1;
    public final static TimeUnit ipBlacklistTTLUnit = TimeUnit.DAYS;

    // ip 访问一个 加上限制注解的接口是，添加的redisKey的前缀，通过该key来记录请求的接口路径，以及ip地址，格式为 prefix+路径+ip
    public final static String ipAccessLimitRecordPrefix = "ipAccessLimitRecord";

    public final static String unAccessLimitCodePrefix = "unAccessLimitCodeKey-";
    public final static long unAccessLimitCodeTTL = 60;
    public final static TimeUnit unAccessLimitCodeUnit = TimeUnit.SECONDS;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Pointcut(value = "execution(* com.xjh.myblog.controller.*.*(..))" +
            "&& @annotation(com.xjh.myblog.annotation.AccessLimit)")
    public void pointCut(){}

    @Before("pointCut()")
    public void accessLimitHandle(JoinPoint joinPoint) {
        // 获取注解
        AccessLimit accessLimit = AopUtil.getAnnotation(joinPoint,AccessLimit.class);
        // 获取request对象
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        // 首先判断该次请求的ip是否在黑名单中
        String ipAddr = ServletUtil.getRequestIpAddr(request);
        // 获取限制请求的redisKey
        String ipAccessLimitKey = getIpAccessLimitRedisKey(ipAddr,request.getRequestURI(),request.getMethod());
        if(hasRedisKey(ipAccessLimitKey)){
            Long count = redisTemplate.opsForValue().increment(ipAccessLimitKey);
            if(count == null){
                throw new MyException(IResultCode.SYSTEM_ERROR.getCode(), "系统错误,sorry");
            }
            // 达到最大请求数
            if(count >= accessLimit.maxCount()){
                // 加入ip黑名单 设置ttl
                String ipBlackRedisKey = getIpBlackRedisKey(ipAddr);
                redisTemplate.opsForValue().set(ipBlackRedisKey,"",ipBlacklistTTL,ipBlacklistTTLUnit);
                throw new MyException(IResultCode.LIMIT_IP_REQUEST.getCode(), "检测到此ip: " + ipAddr +" 恶意访问本站接口,以拒绝访问");
            }
        }else{ // 第一次请求，添加记录到redis中,并设置请求数为1
            redisTemplate.opsForValue().setIfAbsent(ipAccessLimitKey,1,accessLimit.period(),accessLimit.timeUnit());
        }
    }

    // 拦截 黑名单 中的ip,该函数的调用在全局请求切面中完成
    public void interceptBlackIp(String ipAddr){
        String ipBlackRedisKey = getIpBlackRedisKey(ipAddr);
        if(hasRedisKey(ipBlackRedisKey)){
            throw new MyException(IResultCode.LIMIT_IP_REQUEST.getCode(), "检测到此ip: " + ipAddr +" 恶意访问本站接口,以拒绝访问");
        }
    }

    // 获取一个ip对应黑名单的redisKey
    private String getIpBlackRedisKey(String ipAddr){
        return ipBlacklistPrefix + ipAddr;
    }

    // 获取一个存储限制IP访问的redisKey
    private String getIpAccessLimitRedisKey(String ipAddr,String uri,String method){
        return ipAccessLimitRecordPrefix + "-" + method + "-" + uri + "-"+ipAddr;
    }
    private boolean hasRedisKey(String redisKey) {
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        return hasKey != null && hasKey;
    }

    // 生成一个请求解封黑名单IP的验证码 返回图片的base64编码
    public String getUnAccessLimitCode(){
        // 查看是否为黑名单IP
        String ipAddr = ServletUtil.getRequestIpAddr();
        if(!hasRedisKey(getIpBlackRedisKey(ipAddr))){
            throw new MyException("无法获取验证码，该IP访问正常");
        }
        String code = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(code);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            // 写入流中
            ImageIO.write(image,"jpeg",outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            // 将验证码存入redis
            redisTemplate.opsForValue().set(getUnAccessLimitCodeKey(ipAddr),code,unAccessLimitCodeTTL,unAccessLimitCodeUnit);
            return "data:image/jpeg;base64," + base64;
        }catch (IOException e){
            throw new MyException(IResultCode.SYSTEM_ERROR.getCode(), "获取验证码失败");
        }
    }

    // 验证 验证码是否正确
    public boolean verifyUnAccessLimitCode(String code){
        String ipAddr = ServletUtil.getRequestIpAddr();
        String codeRedisKey = getUnAccessLimitCodeKey(ipAddr);
        String value = (String) redisTemplate.opsForValue().get(codeRedisKey);
        if(value == null){
            throw new MyException("请先获取验证码,或验证码已过期");
        }
        boolean isTrue = value.equals(code);
        if(isTrue){
            String ipBlankListKey = getIpBlackRedisKey(ipAddr);
            // 删除对应的限制访问的redisKey和验证码
            redisTemplate.delete(Arrays.asList(codeRedisKey, ipBlankListKey));
        }
        return isTrue;
    }
    // 获取解决ip限制是存储验证码对应的redisKey
    private String getUnAccessLimitCodeKey(String ipAddr){
        return unAccessLimitCodePrefix + ipAddr;
    }
}
