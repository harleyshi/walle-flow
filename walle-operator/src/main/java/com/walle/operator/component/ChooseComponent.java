package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import com.walle.operator.node.Operator;
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
public class ChooseComponent<C extends FlowCtx, O> extends AbstractComponent<C, O> {

    private Operator<C, O> condition;

    public ChooseComponent(String name) {
        super(name);
    }

    @Override
    public O doExecute(C context) {
        return condition.execute(context);
    }
}
