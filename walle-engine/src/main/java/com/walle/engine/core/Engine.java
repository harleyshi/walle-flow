package com.walle.engine.core;

import com.walle.engine.core.executor.GraphExecutor;
import com.walle.operator.FlowCtx;

/**
 * @author harley.shi
 * @date 2025/3/7
 */
public interface Engine<C extends FlowCtx> {

    String name();

    String version();

    GraphExecutor<C> buildExecutor();
}
