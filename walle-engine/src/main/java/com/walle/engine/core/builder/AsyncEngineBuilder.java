package com.walle.engine.core.builder;

import com.walle.engine.core.AsyncEngine;
import com.walle.operator.FlowCtx;
import com.walle.operator.common.constants.Constants;
import com.walle.operator.component.IComponent;
import com.walle.operator.component.PipelineComponent;
import com.walle.operator.node.Node;
import com.walle.operator.utils.AssertUtil;
import com.walle.operator.utils.DAG;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 全异步DAG引擎构建
 * @author harley.shi
 * @date 2025/1/5
 */
public class AsyncEngineBuilder{

    private final String name;

    private final String version;

    private DAG<String> originalDag;

    private Map<String, IComponent<FlowCtx, ?>> components;


    public AsyncEngineBuilder(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void originalDag(DAG<String> originalDag) {
        AssertUtil.notNull(originalDag, "originalDag must not be null!");
        this.originalDag = originalDag;
    }

    public void components(Map<String, IComponent<FlowCtx, ?>> components) {
        AssertUtil.notEmpty(components, "components must not be empty!");
        this.components = components;
    }

    /**
     * 构建DAG引擎
     */
    public AsyncEngine<FlowCtx> buildEngine() {
        AsyncEngine<FlowCtx> dagEngine = new AsyncEngine<>(name, version);
        // 优化DAG
        optimizeDAG(dagEngine);

        dagEngine.printGraph();
        return dagEngine;
    }

    /**
     * 优化DAG
     */
    public void optimizeDAG(AsyncEngine<FlowCtx> dagEngine){
        // 构建原始节点到合并节点的映射：如果节点属于某条串行路径，则映射到该路径的合并节点
        Map<String, Node> mergedMapping = new HashMap<>();

        // 1. 找串行路径并合并
        List<List<String>> serialPaths = findSerialPaths();

        for (List<String> nodes : serialPaths) {
            Node mergedNode = buildNodes(nodes);
            dagEngine.addNode(mergedNode);
            for (String nodeId : nodes) {
                mergedMapping.put(nodeId, mergedNode);
            }
        }

        // 2. 加入未合并的节点
        for (String node : originalDag.getAllNodes()) {
            if (!mergedMapping.containsKey(node)) {
                dagEngine.addNode(buildNode(node));
            }
        }

        // 3. 重新连边
        for (String node : originalDag.getAllNodes()) {
            Node fromNode = mergedMapping.getOrDefault(node, buildNode(node));
            for (DAG.Edge<String> edge : originalDag.getOutgoingEdges(node)) {
                Node toNode = mergedMapping.getOrDefault(edge.getTarget(), buildNode(edge.getTarget()));
                if (!fromNode.equals(toNode)) {
                    dagEngine.addEdge(fromNode, toNode, edge.getData());
                }
            }
        }
    }

    /**
     * 构建单节点
     */
    private Node buildNode(String nodeId) {
        return new Node(nodeId, components.get(nodeId));
    }

    /**
     * 构建合并节点
     */
    private Node buildNodes(List<String> nodeIds) {
        List<String> pipelineNames = new ArrayList<>();

        List<IComponent<FlowCtx, ?>> componentList = new ArrayList<>();
        nodeIds.forEach(nodeId -> {
            IComponent<FlowCtx, ?> component = components.get(nodeId);
            pipelineNames.add(component.name());
            componentList.add(component);
        });

        String pipelineName = Arrays.toString(pipelineNames.toArray());
        PipelineComponent<FlowCtx, ?> pipelineComponent = new PipelineComponent<>(pipelineName, componentList);
        return new Node(nodeIds, pipelineComponent);
    }

    /**
     * 找串行路径并合并
     */
    private List<List<String>> findSerialPaths() {
        List<List<String>> paths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        // 获取拓扑排序的节点顺序
        List<String> sortedNodes = originalDag.topologicalSortKahn();
        // 去掉开始阶段
        if (sortedNodes.size() > 2) {
            sortedNodes = sortedNodes.subList(1, sortedNodes.size());
        } else {
            sortedNodes = Collections.emptyList();
        }
        for (String node : sortedNodes) {
            if (visited.contains(node)) continue;
            List<String> path = new ArrayList<>();
            String current = node;
            // 判断当前节点是否满足作为串行路径一部分的条件
            while (current != null && !visited.contains(current)) {
                path.add(current);
                visited.add(current);

                current = getSingleSuccessor(current);
            }
            // 如果路径长度大于1，就认为找到了一个串行路径
            if (path.size() > 1){
                paths.add(path);
            }
        }
        return paths;
    }

    private String getSingleSuccessor(String node) {
        // 检查节点是否有唯一的后继，且该后继有唯一的前驱
        Set<String> successors = originalDag.getSuccessors(node);
        if (successors.size() != 1) return null;

        // 检查后继节点是否只有当前节点作为前驱
        String next = successors.iterator().next();
        // 如果是end节点，则返回null
        if(next.equals(Constants.END_NODE)){
            return null;
        }
        return originalDag.getPredecessors(next).size() == 1 ? next : null;
    }
}
