package com.xjh.myblog.app.controller;

import com.xjh.myblog.common.api.CommonResult;
import com.xjh.myblog.app.entity.Admin;
import com.xjh.myblog.app.service.AdminService;
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
    public CommonResult login(@Valid @RequestBody Admin adminVo){
        String token = adminService.login(adminVo);
        return CommonResult.success().data("token",token);
    }
}
