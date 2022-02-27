package com.xjh.myblog.constant.ENUM;

public enum IResultCode {
    SUCCESS(200),
    ERROR(404);

    private final Integer code;
    IResultCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
