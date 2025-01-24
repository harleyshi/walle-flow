package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import com.walle.operator.node.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 标准组件构造器
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
@Getter
@Setter
public class StandardComponent<C extends FlowCtx, O> extends AbstractComponent<C, O> {

    /**
     * 算子
     */
    private Operator<C, O> operator;

    public StandardComponent(String name) {
        super(name);
    }

    public StandardComponent(String name, Operator<C, O> operator) {
        super(name);
        this.operator = operator;
    }

    @Override
    public O doExecute(C context) {
        return operator.execute(context);
    }
}
