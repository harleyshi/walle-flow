package com.flow.engine.executor;

import com.flow.engine.model.FlowCtx;

/**
 * 执行器接口
 * @author harley.shi
 * @date 2024/7/3
 */
public interface IExecutor<C extends FlowCtx> {
    /**
     * 获取执行器名称
     */
    String getName();

    /**
     * 执行器执行方法
     */
    void execute(C context);
}
