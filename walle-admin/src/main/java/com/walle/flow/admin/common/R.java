package com.walle.flow.admin.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Setter
@Getter
public class R<T> implements Serializable {

    private String code;

    private String msg;

    private T data;

    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setCode(ErrorEnum.SUCCESS.getCode());
        r.setMsg(ErrorEnum.SUCCESS.getMessage());
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ErrorEnum.SUCCESS.getCode());
        r.setMsg(ErrorEnum.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(ErrorEnum.SUCCESS.getCode());
        r.setMsg(message);
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.setCode(ErrorEnum.BUSINESS_ERROR.getCode());
        r.setMsg(message);
        return r;
    }

    public static <T> R<T> fail(ErrorEnum errorEnums) {
        R<T> r = new R<>();
        r.setCode(errorEnums.getCode());
        r.setMsg(errorEnums.getMessage());
        return r;
    }


    public static <T> R<T> fail(ErrorEnum errorEnums, String message) {
        R<T> r = new R<>();
        r.setCode(errorEnums.getCode());
        if (ErrorEnum.INTERNAL_SERVER_ERROR.getCode().equals(errorEnums.getCode())) {
            // 如果是系统异常 隐藏具体服务报错信息
            r.setMsg(errorEnums.getMessage());
        }else {
            r.setMsg(message);
        }
        return r;
    }

}
