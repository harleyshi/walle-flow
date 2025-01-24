package com.walle.test.springboot.operator;

import com.walle.operator.ComponentFn;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.node.AbstractOperator;
import com.walle.test.springboot.context.OrderContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author harley.shi
 * @date 2025/1/20
 */
@Service
@ComponentFn(name = "check_top_item", type = NodeType.CONDITION)
public class CheckTopItemNode extends AbstractOperator<OrderContext, Boolean> {

    @Override
    public Boolean doExecute(OrderContext ctx) {
        System.out.println("check_top_item execute");
        return false;
    }

    @Override
    public Boolean doFallback(OrderContext ctx) {
        return null;
    }
}
