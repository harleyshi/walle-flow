package com.flow.engine.model;

import com.flow.engine.operator.Operator;

import java.util.Stack;

/**
 * 上下文抽象类
 * @author harley.shi
 * @date 2024/6/25
 */
public abstract class AbstractFlowCtx implements FlowCtx {

    /**
     * 回滚算子栈
     */
    private final Stack<Operator<?>> rollbackInvokers = new Stack<>();

    /**
     * 流程是否有异常：true-是，false-否
     */
    private volatile boolean hasException;

    @Override
    public Object getScriptParams(){
        return this;
    }

    @Override
    public <C extends FlowCtx> void addRollback(Operator<C> invoker) {
        if (invoker == null) {
            return;
        }
        rollbackInvokers.push(invoker);
    }

    @Override
    public Stack<Operator<?>> rollbackStacks() {
        return this.rollbackInvokers;
    }

    @Override
    public boolean hasException() {
        return hasException;
    }

    @Override
    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }
}
