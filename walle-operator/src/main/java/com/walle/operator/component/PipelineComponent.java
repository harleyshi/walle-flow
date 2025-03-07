package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准组件构造器
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
public class PipelineComponent<C extends FlowCtx, O> implements IComponent<C, O> {
    /**
     * 组件名
     */
    private final String name;

    /**
     * 超时时间
     */
    private final List<IComponent<C, ?>> componentList;

    public PipelineComponent(String name, List<IComponent<C, ?>> componentList) {
        this.name = name;
        this.componentList = componentList;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public O execute(C ctx) {
        for (IComponent<C, ?> component : componentList) {
            component.execute(ctx);
        }
        return null;
    }
}
