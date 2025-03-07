package com.walle.engine.common.enums;

import lombok.Getter;

/**
 * engine execution mode
 * @author harley.shi
 * @date 2025/1/23
 */
@Getter
public enum ExecutionMode {
    SYNC("sync", "同步执行"),

    // BATCH("batch", "批量执行"),

    ASYNC("async", "全异步执行");

    private final String code;

    private final String desc;

    ExecutionMode(String code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public static ExecutionMode getByCode(String code) {
        for (ExecutionMode mode : ExecutionMode.values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        return null;
    }

}