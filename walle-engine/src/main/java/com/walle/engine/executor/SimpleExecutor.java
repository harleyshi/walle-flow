package com.walle.engine.executor;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;

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
        //this.components = new ArrayList<>(dagEngine.getComponents().values());
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
        List<IComponent<C, ?>> componentList = new ArrayList<>();
        Map<String, Integer> inDegrees = dagEngine.copySimpleInDegrees();
        Queue<String> queue = new LinkedList<>(dagEngine.getNodesWithZeroInDegree());

        while (!queue.isEmpty()) {
            String nodeId = queue.poll();
            componentList.add(dagEngine.getComponent(nodeId));
            for (DAGEngine.Edge<String> edge : dagEngine.getNeighbors(nodeId)) {
                String neighbor = edge.getTarget();
                int newInDegree = inDegrees.get(neighbor) - 1;
                inDegrees.put(neighbor, newInDegree);
                if (newInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }
        // 如果图中存在环，则拓扑排序无法完成
        if (componentList.size() != dagEngine.getAllNodes().size()) {
            throw new IllegalStateException("Graph has at least one cycle");
        }
        return componentList;
    }
}
