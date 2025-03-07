package com.walle.operator.node;

import com.walle.operator.FlowCtx;

/**
 * @author harley.shi
 * @date 2025/1/13
 */
public abstract class FallbackOperator<C extends FlowCtx, O> implements Operator<C, O> {

    @Override
    public O execute(C ctx) {
        try {
            return doExecute(ctx);
        }catch (Exception e){
            return doFallback(ctx);
        }
    }

    /**
     * 执行节点
     */
    public abstract O doExecute(C ctx);

    /**
     * 处理失败
     */
    public abstract O doFallback(C ctx);

}
