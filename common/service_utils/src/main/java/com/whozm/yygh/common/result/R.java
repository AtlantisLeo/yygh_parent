package com.whozm.yygh.common.result;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/14
 */
@Getter
@ToString
public class R {

    private Integer code;
    private Boolean success;
    private String message;
    private Map<String,Object> data = new HashMap<String,Object>();

    private R(){}

    public static R ok(){
        R r = new R();
        r.code=REnum.SUCCESS.getCode();
        r.success=REnum.SUCCESS.getFlag();
        r.message=REnum.SUCCESS.getMessage();
        return r;
    }

    public static R error(){
        R r = new R();
        r.code=REnum.ERROR.getCode();
        r.success=REnum.ERROR.getFlag();
        r.message=REnum.ERROR.getMessage();
        return r;
    }

    public R code(Integer code){
        this.code=code;
        return this;
    }
    public R success(Boolean success){
        this.success=success;
        return this;
    }
    public R message(String message){
        this.message=message;
        return this;
    }
    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }
    public R data(Map<String,Object> map){
        this.data=map;
        return this;
    }
}
