package com.flow.engine.component;

import com.flow.engine.model.FlowCtx;

public interface IComponent<T, C extends FlowCtx>{

    T execute(C context);
}
