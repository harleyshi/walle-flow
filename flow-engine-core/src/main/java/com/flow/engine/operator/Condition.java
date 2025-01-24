package com.flow.engine.operator;

import com.flow.engine.model.FlowCtx;

/**
 * 条件算子函数接口
 * @author harley.shi
 * @date 2024/7/1
 */
@FunctionalInterface
public interface Condition<T, C extends FlowCtx> {

     T exec(C context);
}
