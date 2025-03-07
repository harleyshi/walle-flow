package com.walle.operator.node;

import com.walle.operator.FlowCtx;

/**
 * @author harley.shi
 * @date 2025/1/13
 */
public abstract class AbstractOperator<C extends FlowCtx, O> implements Operator<C, O> {

    @Override
    public O execute(C ctx) {
        return doExecute(ctx);
    }

    /**
     * 执行节点
     */
    public abstract O doExecute(C ctx);

}
