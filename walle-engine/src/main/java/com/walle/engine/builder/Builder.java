package com.walle.engine.builder;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;

/**
 * 构造器接口，用于构建组件
 * @author harley.shi
 * @date 2024/7/1
 */
public interface Builder<C extends FlowCtx, O> {

   IComponent<C, O> build();

}
