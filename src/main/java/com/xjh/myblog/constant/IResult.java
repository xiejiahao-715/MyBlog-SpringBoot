package com.xjh.myblog.constant;

import com.xjh.myblog.constant.ENUM.IResultCode;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

// 统一的返回结果
@Data
public class IResult {
    private Boolean success;
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<>();

    private IResult(){}
    // 成功静态方法
    public static IResult success(){
        IResult result = new IResult();
        result.success = true;
        result.code = IResultCode.SUCCESS.getCode();
        return result;
    }
    // 错误静态方法
    public static IResult error(){
        IResult result = new IResult();
        result.success = false;
        result.code = IResultCode.ERROR.getCode();
        return result;
    }
    public IResult success(Boolean success){
        this.success = success;
        return this;
    }
    public IResult code(Integer code){
        this.code = code;
        return this;
    }
    public IResult message(String message){
        this.message = message;
        return this;
    }
    public IResult data(String key,Object value){
        this.data.put(key,value);
        return this;
    }
    public IResult data(Map<String,Object> map){
        if(map != null){
            this.data = map;
        }
        return this;
    }

}
