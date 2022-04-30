package com.xjh.myblog.common.api;

public enum ResultCode {
    SUCCESS(200,"请求成功"),
    ERROR(404,"请求失败"),
    VALID_ERROR(202,"表单验证异常"),
    SYSTEM_ERROR(203,"服务器内部异常(处理业务逻辑之外没有预料到的错误)"),
    ADMIN_STATUS_ERROR(201,"管理员状态异常"),
    REDIS_ERROR(206,"redis服务异常，请联系管理员"),
    LIMIT_IP_REQUEST(509,"限制ip访问的状态码");

    private final Integer code;
    private final String message;
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
}
