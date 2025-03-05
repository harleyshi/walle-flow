package com.walle.test.springboot.operator;

import com.walle.operator.ComponentFn;
import com.walle.operator.common.enums.ProcessType;
import com.walle.operator.node.AbstractOperator;
import com.walle.test.springboot.context.OrderContext;
import org.springframework.stereotype.Service;

/**
 * @author harley.shi
 * @date 2025/1/20
 */
@Service
@ComponentFn(name = "check_top_item", type = ProcessType.CONDITION)
public class CheckTopItemNode extends AbstractOperator<OrderContext, Boolean> {

    @Override
    public Boolean doExecute(OrderContext ctx) {
        System.out.println(String.format("[%s]check_top_item execute", Thread.currentThread().getName()));
        return false;
    }

    @Override
    public Boolean doFallback(OrderContext ctx) {
        return null;
    }
}
