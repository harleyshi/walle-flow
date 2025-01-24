package com.flow.engine.script;

/**
 * @author harley.shi
 * @date 2024/10/29
 */
public interface Describable {

    default String describe() {
        return this.getClass().getName();
    }
}
