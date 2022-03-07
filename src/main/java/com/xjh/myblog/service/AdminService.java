package com.xjh.myblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjh.myblog.entity.Admin;

public interface AdminService extends IService<Admin> {
    /**
     * / 管理员登录接口
     * @param admin 前端传递的登录表单
     * @return  返回token
     */
    String login(Admin admin);
}
