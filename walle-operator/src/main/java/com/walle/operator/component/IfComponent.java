package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import com.walle.operator.node.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class IfComponent<C extends FlowCtx, O> extends AbstractComponent<C, O> {

    private Operator<C, O> condition;

    public IfComponent(String name) {
        super(name);
    }

    @Override
    public O doExecute(C context) {
        return condition.execute(context);
    }
}
