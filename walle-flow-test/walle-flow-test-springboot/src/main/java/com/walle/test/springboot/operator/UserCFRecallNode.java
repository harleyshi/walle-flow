package com.walle.test.springboot.operator;

import com.walle.operator.ComponentFn;
import com.walle.operator.common.enums.ProcessType;
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
@ComponentFn(name = "user_cf_recall", type = ProcessType.STANDARD)
public class UserCFRecallNode extends AbstractOperator<OrderContext, List<String>> {
    @Override
    public List<String> doExecute(OrderContext ctx) {
        System.out.println(String.format("[%s]user_cf_recall execute", Thread.currentThread().getName()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add("user_cf_recall_item_" + i);
        }
        ctx.addItems(result);
        return result;
    }

    @Override
    public List<String> doFallback(OrderContext ctx) {
        return null;
    }
}
