package com.walle.test.springboot.context;

import com.walle.operator.AbstractFlowCtx;
import com.walle.operator.FlowCtx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author harley.shi
 * @date 2025/1/20
 */
public class OrderContext extends AbstractFlowCtx {

    private List<String> items = new CopyOnWriteArrayList<>();

    public void addItems(List<String> items) {
        this.items.addAll(items);
    }

    public List<String> getItems() {
        return items;
    }
}
