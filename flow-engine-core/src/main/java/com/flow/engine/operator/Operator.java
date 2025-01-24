package com.flow.engine.operator;

import com.flow.engine.model.FlowCtx;

/**
 * 算子函数接口
 * @see ComponentFn 注解的方法必须返回Operator接口的实例
 * @author harley.shi
 * @date 2024/7/1
 */
@FunctionalInterface
public interface Operator<C extends FlowCtx> {

     void exec(C context);
}
