package com.xjh.myblog.controller;

import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.Admin;
import com.xjh.myblog.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/blog-admin")
public class LoginController {
    @Autowired
    AdminService adminService;

    @PostMapping("/login")
    public IResult login(@RequestBody Admin adminVo){
        String token = adminService.login(adminVo);
        return IResult.success().data("token",token);
    }
}
