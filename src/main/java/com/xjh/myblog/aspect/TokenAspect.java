package com.xjh.myblog.aspect;

import com.xjh.myblog.ENUM.IResultCode;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.utils.ServletUtil;
import com.xjh.myblog.utils.TokenUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@Order(1)
public class TokenAspect {
    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;

    // 切点为 controller包下所有带有@TokenPermission注解的方法
    @Pointcut(value = "execution(* com.xjh.myblog.controller.*.*(..)) " +
            "&& @annotation(com.xjh.myblog.annotation.TokenPermission)")
    public void pointCut(){}

    @Before("pointCut()")
    public void checkToken(){
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        String token = request.getHeader("token");
        String uid = TokenUtil.verifyTokenByUid(token);
        if(uid == null){
            throw new MyException(IResultCode.ADMIN_STATUS_ERROR.getCode(),"用户未登录");
        }
        // 获取redis中存储的token
        String redisToken =  (String) redisTemplate.opsForValue().get(TokenUtil.getRedisKey(uid));
        if(!Objects.equals(redisToken, token)){
            throw new MyException(IResultCode.ADMIN_STATUS_ERROR.getCode(),"用户登录已过期");
        }
    }
}
