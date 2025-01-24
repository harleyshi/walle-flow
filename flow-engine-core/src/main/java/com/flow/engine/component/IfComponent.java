package com.flow.engine.component;

import com.flow.engine.model.FlowCtx;
import com.flow.engine.operator.Condition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class IfComponent<T, C extends FlowCtx> extends AbstractComponent<T, C> {

    private Condition<T, C> condition;

    public IfComponent(String name) {
        super(name);
    }

    @Override
    public T doExecute(C context) {
        return condition.exec(context);
    }
}
