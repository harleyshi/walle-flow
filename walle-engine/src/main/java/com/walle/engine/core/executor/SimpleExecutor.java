package com.walle.engine.core.executor;

import com.walle.engine.core.SimpleEngine;
import com.walle.operator.FlowCtx;
import com.walle.operator.component.PipelineComponent;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class SimpleExecutor<C extends FlowCtx> implements GraphExecutor<C> {

    private final String name;

    private final PipelineComponent<FlowCtx, ?> pipelineComponent;

    public SimpleExecutor(SimpleEngine<C> engine) {
        this.name = engine.name();
        this.pipelineComponent = engine.getPipelineComponent();
    }

    @Override
    public void execute(C context) {
        pipelineComponent.execute(context);
    }

    @Override
    public String getName() {
        return name;
    }
}
