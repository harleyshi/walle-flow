package com.walle.operator.common.enums;

import lombok.Getter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
public enum NodeType {
    START("start", "开始节点"),

    STANDARD("standard", "标准节点"),

    CONDITION("condition", "条件节点"),

    SCRIPT("script", "脚本节点"),

    END("end", "结束节点"),
    ;

    private final String code;

    private final String desc;

    NodeType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NodeType getByCode(String code) {
        for (NodeType nodeType : NodeType.values()) {
            if (nodeType.getCode().equals(code)) {
                return nodeType;
            }
        }
        return null;
    }
}
