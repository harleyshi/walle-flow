package com.walle.engine;

import com.walle.engine.executor.GraphExecutor;
import com.walle.operator.FlowCtx;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
public interface EngineManager {

    void load();

    GraphExecutor<FlowCtx> getEngineExecutor(String engineName);
}
