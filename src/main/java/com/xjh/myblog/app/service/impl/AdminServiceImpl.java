package com.xjh.myblog.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjh.myblog.app.entity.Admin;
import com.xjh.myblog.app.cacheservice.AdminCacheService;
import com.xjh.myblog.common.exception.MyException;
import com.xjh.myblog.app.mapper.AdminMapper;
import com.xjh.myblog.app.service.AdminService;
import com.xjh.myblog.app.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminCacheService adminCacheService;

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

        // 获取用户id
        String uid = realAdmin.getUid().toString();
        String token = TokenUtil.buildToken(uid);
        // 将token存入redis
        adminCacheService.setToken(uid,token);
        return token;
    }
}
