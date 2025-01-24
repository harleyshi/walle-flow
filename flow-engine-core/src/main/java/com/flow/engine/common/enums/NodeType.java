package com.flow.engine.common.enums;

import lombok.Getter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
public enum NodeType {

//    ENGINE("engine", "引擎节点"),

//    ENGINE_NAME("engineName", "引擎名称节点"),

//    PIPELINE("pipeline", "流程节点"),

    IF("if", "条件节点"),

    CHOOSE("choose", "多条件节点"),

    COMPONENT("component", "组件节点"),

//    CONFIG_PARAMS("configParams", "配置参数节点"),

    SCRIPT("script", "脚本节点")
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
