package com.walle.test.springboot.operator;

import com.walle.operator.ComponentFn;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.node.AbstractOperator;
import com.walle.test.springboot.context.OrderContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author harley.shi
 * @date 2025/1/20
 */
@Service
@ComponentFn(name = "hot_recall", type = NodeType.STANDARD)
public class HotRecallNode extends AbstractOperator<OrderContext, List<String>> {
    @Override
    public List<String> doExecute(OrderContext ctx) {
        System.out.println("hot_recall execute");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add("hot_recall_item_" + i);
        }
        ctx.addItems(result);
        return result;
    }

    @Override
    public List<String> doFallback(OrderContext ctx) {
        return null;
    }
}
