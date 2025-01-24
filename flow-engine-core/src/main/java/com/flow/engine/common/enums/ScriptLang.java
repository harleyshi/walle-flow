package com.flow.engine.common.enums;

import lombok.Getter;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
@Getter
public enum ScriptLang {

    GROOVY("groovy", "groovy脚本"),

    JANINO("janino", "janino脚本"),

    ;

    private final String code;

    private final String desc;

    ScriptLang(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
