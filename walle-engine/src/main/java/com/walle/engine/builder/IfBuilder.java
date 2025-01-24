package com.walle.engine.builder;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.IfComponent;
import com.walle.operator.node.Operator;
import com.walle.operator.utils.AssertUtil;

/**
 * if条件构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class IfBuilder<C extends FlowCtx, O> implements Builder<C, O> {

    private final String name;

    private Operator<C, O> condition;

    public IfBuilder(String name) {
        this.name = name;
    }

    public IfBuilder<C, O> condition(Operator<C, O> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    @Override
    public IfComponent<C, O> build() {
        AssertUtil.notNull(condition, "condition must not be null");

        IfComponent<C, O> ifComponent = new IfComponent<>(name);
        ifComponent.setCondition(condition);
        return ifComponent;
    }
}
