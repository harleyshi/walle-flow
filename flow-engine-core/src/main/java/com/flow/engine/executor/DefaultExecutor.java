package com.flow.engine.executor;

import com.flow.engine.model.FlowCtx;
import com.flow.engine.component.IComponent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class DefaultExecutor<C extends FlowCtx> extends AbstractExecutor<C> {
    private final String name;
    private final List<IComponent<?, C>> components;

    public DefaultExecutor(DAGEngine<C> dagEngine) {
        this.name = dagEngine.getName();
        this.components = new ArrayList<>(dagEngine.getComponents().values());
//        this.components = topologicalSortKahn(dagEngine);
    }

    @Override
    void doExecute(C context) {
        for (IComponent<?, C> component : components) {
            // 若出现异常，后续的节点不再执行
            if(context.hasException()){
                return;
            }
            // TODO 检查是否满足分支条件
            try {
                component.execute(context);
            } catch (Throwable ex) {
                // 忽略异常：直接返回空结果
//                if(isIgnoreException()){
//                    // 如果节点设置忽略异常的话，当节点发送异常时直接忽略
//                    log.error("[{}] operator execute error, but ignore it. error message: {}", getName(), ex.getMessage());
//                    return;
//                }
                context.setHasException(true);
                throw ex;
            }
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
    public List<IComponent<?, C>> topologicalSortKahn(DAGEngine<C> dagEngine) {
        List<IComponent<?, C>> componentList = new ArrayList<>();
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
