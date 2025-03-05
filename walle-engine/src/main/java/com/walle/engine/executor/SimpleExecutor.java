package com.walle.engine.executor;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;
import com.walle.operator.node.Node;

import java.util.*;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class SimpleExecutor<C extends FlowCtx> implements GraphExecutor<C> {
    private final String name;
    private final List<IComponent<C, ?>> components;

    public SimpleExecutor(DAGEngine<C> dagEngine) {
        this.name = dagEngine.getName();
        this.components = topologicalSortKahn(dagEngine);
    }

    @Override
    public void execute(C context) {
        for (IComponent<C, ?> component : components) {
            component.execute(context);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 使用Kahn算法对图进行拓扑排序
     * @param dagEngine 有向图引擎
     * @return 拓扑排序结果
     * @throws IllegalStateException 如果图中存在环
     */
    public List<IComponent<C, ?>> topologicalSortKahn(DAGEngine<C> dagEngine) {
        List<Node> sortedNodes = dagEngine.getDagGraph().topologicalSortKahn();

        List<IComponent<C, ?>> componentList = new ArrayList<>();
        sortedNodes.forEach(node -> {
            componentList.add(dagEngine.getComponent(node.getNodeId()));
        });
        return componentList;
    }
}
