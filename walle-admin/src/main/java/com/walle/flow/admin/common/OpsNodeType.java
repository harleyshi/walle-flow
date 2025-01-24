package com.walle.flow.admin.common;

import lombok.Getter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
public enum OpsNodeType {
    STANDARD("standard", "标准节点"),

    CONDITION("condition", "条件节点")
    ;

    private final String code;

    private final String desc;

    OpsNodeType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OpsNodeType getByCode(String code) {
        for (OpsNodeType nodeType : OpsNodeType.values()) {
            if (nodeType.getCode().equals(code)) {
                return nodeType;
            }
        }
        return null;
    }
}
