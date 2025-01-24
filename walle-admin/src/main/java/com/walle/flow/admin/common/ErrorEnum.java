package com.walle.flow.admin.common;

import lombok.Getter;

/**
 * 异常枚举
 */
@Getter
public enum ErrorEnum {

    SUCCESS(200, "0000"),

    // 参数异常,
    BAD_REQUEST(400, "0400"),

    // 权限错误
    UNAUTHORIZED(401,"0401"),

    // 禁止访问
    FORBIDDEN(403,"0403"),

    // 找不到页面
    NOT_FOUND(404,"0404"),

    // 目标资源不支持该请求方法
    METHOD_NOT_ALLOWED(405,"0405"),

    // Content-Type 不支持
    NOT_ACCEPTABLE(406,"0406"),

    // Media Type 不支持
    UNSUPPORTED_MEDIA_TYPE(415,"0415"),

    // 限流
    TOO_MANY_REQUEST(429,"0429"),

    // 系统异常
    INTERNAL_SERVER_ERROR(500,"0500"),

    // 业务异常
    BUSINESS_ERROR(200, "1000")
    ;

    private final Integer httpStatus;

    private final String code;

    ErrorEnum(Integer httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public static ErrorEnum getErrorEnumsByCode(String code) {
        ErrorEnum[] values = ErrorEnum.values();
        for (ErrorEnum enums : values) {
            if (enums.getCode().equals(code)) {
                return enums;
            }
        }
        return null;
    }

    public static Integer getHttpStatusByCode(String code) {
        ErrorEnum[] values = ErrorEnum.values();
        for (ErrorEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getHttpStatus();
            }
        }
        return null;
    }

    public String getMessage() {
        return String.join(" ", this.name().toLowerCase().split("_"));
    }

    public static String getMessage(ErrorEnum errorEnums) {
        return String.join(" ", errorEnums.name().toLowerCase().split("_"));
    }
}
