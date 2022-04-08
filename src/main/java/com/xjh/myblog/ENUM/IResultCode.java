package com.xjh.myblog.ENUM;

public enum IResultCode {
    // 请求成功
    SUCCESS(200),
    // 请求失败
    ERROR(404),
    // 表单验证异常
    VALID_ERROR(202),
    // 服务器内部异常(处理业务逻辑之外没有预料到的错误)
    SYSTEM_ERROR(203),
    // 管理员状态异常
    ADMIN_STATUS_ERROR(201),
    // 限制ip访问的状态码
    LIMIT_IP_REQUEST(509);

    private final Integer code;
    IResultCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
