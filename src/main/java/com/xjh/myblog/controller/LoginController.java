package com.xjh.myblog.controller;

import com.xjh.myblog.constant.IResult;
import com.xjh.myblog.entity.Admin;
import com.xjh.myblog.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "管理员登录相关接口")
@CrossOrigin
@RestController
@RequestMapping("/blogAdmin")
public class LoginController {
    @Autowired
    private AdminService adminService;

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public IResult login(@Valid @RequestBody Admin adminVo){
        String token = adminService.login(adminVo);
        return IResult.success().data("token",token);
    }
}
