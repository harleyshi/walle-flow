package com.walle.engine.common.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {

    PUBLISHED("published", "发布"),

    UNAVAILABLE("unavailable", "下架")

    ;

    private final String code;
    private final String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getDesc();
            }
        }
        return "";
    }

    public static StatusEnum getByCode(String code) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
