package com.xjh.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.entity.Admin;
import com.xjh.myblog.exceptionhandler.MyException;
import com.xjh.myblog.mapper.AdminMapper;
import com.xjh.myblog.service.AdminService;
import com.xjh.myblog.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public String login(Admin admin) {
        String username = admin.getUsername();
        String password = admin.getPassword();
        Admin realAdmin = this.getOne(new QueryWrapper<Admin>().eq("username",username));
        if(realAdmin == null)
            throw new MyException("账户不存在");
        if(!password.equals(realAdmin.getPassword()))
            throw new MyException("密码错误");

        String uid = realAdmin.getUid().toString();
        String redisKey = TokenUtil.getRedisKey(uid);
        String token = TokenUtil.buildToken(uid);
        // 将token存入redis
        redisTemplate.opsForValue().set(redisKey,token,TokenUtil.TOKEN_EXPIRE_TIME);
        return token;
    }
}
