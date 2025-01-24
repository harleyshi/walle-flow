package com.flow.engine.component.builder;


import com.flow.engine.operator.Condition;
import com.flow.engine.component.ChooseComponent;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.utils.AssertUtil;

/**
 * choose构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class ChooseBuilder<T, C extends FlowCtx> implements Builder<T, C> {

    private final String name;
    private Condition<T, C> condition;

    public ChooseBuilder(String name) {
        this.name = name;
    }

    public ChooseBuilder<T, C> condition(Condition<T, C> condition) {
        AssertUtil.notNull(condition, "condition must not be null!");
        this.condition = condition;
        return this;
    }

    @Override
    public ChooseComponent<T, C> build() {
        AssertUtil.notNull(condition, "condition must not be null!");
        ChooseComponent<T, C> chooseComponent = new ChooseComponent<>(name);
        chooseComponent.setCondition(condition);
        return chooseComponent;
    }
}
