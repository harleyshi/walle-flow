package com.flow.engine.model;


import com.flow.engine.operator.Operator;

import java.util.Stack;

/**
 * 上下文接口
 * @author harley.shi
 * @date 2024/6/25
 */
public interface FlowCtx {

    /**
     * 脚本参数
     */
    Object getScriptParams();

    /**
     * 是否有异常
     */
    boolean hasException();

    /**
     * 设置是否有异常
     */
    void setHasException(boolean hasException);

    /**
     * 获取异常算子栈
     */
    Stack<Operator<?>> rollbackStacks();

    /**
     * 添加回滚算子
     */
    <C extends FlowCtx> void addRollback(Operator<C> rollback);
}
