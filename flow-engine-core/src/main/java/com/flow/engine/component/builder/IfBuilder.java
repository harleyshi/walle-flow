package com.flow.engine.component.builder;

import com.flow.engine.component.*;
import com.flow.engine.operator.Condition;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.utils.AssertUtil;

/**
 * if条件构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class IfBuilder<T, C extends FlowCtx> implements Builder<T, C> {

    private final String name;

    private Condition<T, C> condition;

    public IfBuilder(String name) {
        this.name = name;
    }

    public IfBuilder<T, C> condition(Condition<T, C> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    @Override
    public IfComponent<T, C> build() {
        AssertUtil.notNull(condition, "condition must not be null");

        IfComponent<T, C> ifComponent = new IfComponent<>(name);
        ifComponent.setCondition(condition);
        return ifComponent;
    }
}
