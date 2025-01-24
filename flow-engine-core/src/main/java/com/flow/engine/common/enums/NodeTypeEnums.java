package com.flow.engine.common.enums;

import lombok.Getter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
public enum NodeTypeEnums {
    START("start", "开始节点"),

    STANDARD("standard", "标准节点"),

    DOWNGRADE("downgrade", "降级节点"),

    ROLLBACK("rollback", "回滚节点"),

    CONDITION("condition", "条件节点"),

    SCRIPT("script", "脚本节点"),

    END("end", "结束节点"),
    ;

    private final String code;

    private final String desc;

    NodeTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NodeTypeEnums getByCode(String code) {
        for (NodeTypeEnums nodeType : NodeTypeEnums.values()) {
            if (nodeType.getCode().equals(code)) {
                return nodeType;
            }
        }
        return null;
    }
}
