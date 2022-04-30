package com.xjh.myblog.app.aspect;

import com.xjh.myblog.app.cacheservice.AdminCacheService;
import com.xjh.myblog.common.api.ResultCode;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.common.utils.ServletUtil;
import com.xjh.myblog.app.utils.TokenUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@Order(1)
public class TokenAspect {
    @Autowired
    private AdminCacheService adminCacheService;

    // 切点为 controller包下所有带有@TokenPermission注解的方法
    @Pointcut(value = "execution(* com.xjh.myblog.app.controller.*.*(..)) " +
            "&& @annotation(com.xjh.myblog.app.annotation.TokenPermission)")
    public void pointCut(){}

    @Before("pointCut()")
    public void checkToken(){
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        String token = request.getHeader(TokenUtil.TOKEN_HEADER_NAME);
        String uid = TokenUtil.verifyTokenByUid(token);
        if(uid == null){
            throw new MyException(ResultCode.ADMIN_STATUS_ERROR.getCode(),"用户未登录");
        }
        // 获取redis中存储的token
        String redisTokenValue = adminCacheService.getToken(uid);
        if(!Objects.equals(redisTokenValue, token)){
            throw new MyException(ResultCode.ADMIN_STATUS_ERROR.getCode(),"用户登录已过期");
        }
    }
}
