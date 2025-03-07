package com.walle.engine.core;

import com.walle.engine.core.executor.GraphExecutor;
import com.walle.engine.core.executor.SimpleExecutor;
import com.walle.operator.FlowCtx;
import com.walle.operator.component.PipelineComponent;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class SimpleEngine<C extends FlowCtx> implements Engine<C> {

    /*
     * name
     */
    private final String name;

    /*
     * version
     */
    private final String version;

    /*
     * pipeline component
     */
    private final PipelineComponent<FlowCtx, ?> pipelineComponent;

    /**
     * constructor of AsyncEngine
     */
    public SimpleEngine(String name, String version, PipelineComponent<FlowCtx, ?> pipelineComponent) {
        this.name = name;
        this.version = version;
        this.pipelineComponent = pipelineComponent;
    }

    /**
     * build executor
     */
    @Override
    public GraphExecutor<C> buildExecutor(){
        return new SimpleExecutor<>(this);
    }

    /**
     * get pipeline component
     */
    public PipelineComponent<FlowCtx, ?> getPipelineComponent() {
        return pipelineComponent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return this.version;
    }
}
