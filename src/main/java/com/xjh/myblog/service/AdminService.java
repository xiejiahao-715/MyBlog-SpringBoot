package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Admin;

public interface AdminService extends IService<Admin> {
    // 登录验证表单 返回token
    String login(Admin admin);
}
