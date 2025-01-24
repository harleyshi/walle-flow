package com.walle.test.springboot.context;

import com.walle.operator.FlowCtx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author harley.shi
 * @date 2025/1/20
 */
public class OrderContext implements FlowCtx {

    private List<String> items = new CopyOnWriteArrayList<>();

    public void addItems(List<String> items) {
        this.items.addAll(items);
    }

    public List<String> getItems() {
        return items;
    }

    @Override
    public String getContextId() {
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }
}
