package com.flow.engine.component;

import com.flow.engine.model.FlowCtx;
import com.flow.engine.operator.Condition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * choose组件
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
@Getter
@Setter
public class ChooseComponent<T, C extends FlowCtx> extends AbstractComponent<T, C> {

    private Condition<T, C> condition;

    public ChooseComponent(String name) {
        super(name);
    }

    @Override
    public T doExecute(C context) {
        return condition.exec(context);
    }
}
