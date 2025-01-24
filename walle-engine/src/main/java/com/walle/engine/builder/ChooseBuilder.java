package com.walle.engine.builder;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.ChooseComponent;
import com.walle.operator.node.Operator;
import com.walle.operator.utils.AssertUtil;

/**
 * choose构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class ChooseBuilder<C extends FlowCtx, O> implements Builder<C, O> {

    private final String name;
    private Operator<C, O> condition;

    public ChooseBuilder(String name) {
        this.name = name;
    }

    public ChooseBuilder<C, O> condition(Operator<C, O> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    @Override
    public ChooseComponent<C, O> build() {
        AssertUtil.notNull(condition, "condition must not be null!");
        ChooseComponent<C, O> chooseComponent = new ChooseComponent<>(name);
        chooseComponent.setCondition(condition);
        return chooseComponent;
    }
}
