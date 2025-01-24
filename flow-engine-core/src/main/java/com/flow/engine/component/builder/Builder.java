package com.flow.engine.component.builder;


import com.flow.engine.model.FlowCtx;
import com.flow.engine.component.IComponent;

/**
 * 构造器接口，用于构建组件
 * @author harley.shi
 * @date 2024/7/1
 */
public interface Builder<T, C extends FlowCtx> {

   IComponent<T, C> build();

}
