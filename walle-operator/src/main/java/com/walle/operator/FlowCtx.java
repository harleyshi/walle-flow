package com.walle.operator;

/**
 * @author harley.shi
 * @date 2025/1/13
 */
public interface FlowCtx {
    /**
     * 获取上下文 ID（如请求 ID）
     */
    String getContextId();

    /**
     * 设置上下文属性
     */
    void setAttribute(String key, Object value);

    /**
     * 获取上下文属性
     */
    Object getAttribute(String key);
}
