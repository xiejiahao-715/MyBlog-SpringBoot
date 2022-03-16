package com.xjh.myblog.constant.ENUM;

public enum IResultCode {
    // 请求成功
    SUCCESS(200),
    // 请求失败
    ERROR(404),
    // 管理员状态异常
    ADMIN_STATUS_ERROR(201);

    private final Integer code;
    IResultCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
