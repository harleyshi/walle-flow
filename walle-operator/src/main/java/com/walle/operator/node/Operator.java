package com.walle.operator.node;

import com.walle.operator.FlowCtx;

/**
 * @author harley.shi
 * @date 2025/1/13
 */
public interface Operator<C extends FlowCtx, O> {
    /**
     * 执行 OP 算子的核心逻辑
     */
    O execute(C ctx);
}
