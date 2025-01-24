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
@ComponentFn(name = "top_rerank", type = NodeType.STANDARD)
public class TopReRankNode extends AbstractOperator<OrderContext, List<String>> {

    @Override
    public List<String> doExecute(OrderContext ctx) {
        System.out.println("top_rerank execute");
        return ctx.getItems().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<String> doFallback(OrderContext ctx) {
        return null;
    }
}
