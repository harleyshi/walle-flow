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
@ComponentFn(name = "merge_recall", type = NodeType.STANDARD)
public class MergeRecallNode extends AbstractOperator<OrderContext, List<String>> {

    @Override
    public List<String> doExecute(OrderContext ctx) {
        System.out.println("merge_recall execute");
        return ctx.getItems().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> doFallback(OrderContext ctx) {
        return null;
    }
}
