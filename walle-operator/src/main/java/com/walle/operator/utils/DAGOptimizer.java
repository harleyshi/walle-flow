//package com.walle.operator.utils;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.common.constants.Constants;
//import com.walle.operator.component.PipelineComponent;
//import com.walle.operator.component.IComponent;
//import com.walle.operator.node.Node;
//
//import java.util.*;
//
///**
// * 图优化器：合并串行路径
// * @author harley.shi
// * @date 2025/1/5
// */
//public class DAGOptimizer<T> {
//    private final DAG<T> originalDag;
//
//    private final Map<String, IComponent<FlowCtx, ?>> components;
//
//    public DAGOptimizer(DAG<T> graph, Map<String, IComponent<FlowCtx, ?>> components) {
//        this.originalDag = graph;
//        this.components = components;
//    }
//
//    public DAG<Node> optimize() {
//        DAG<Node> optimized = new DAG<>();
//
//        // 构建原始节点到合并节点的映射：如果节点属于某条串行路径，则映射到该路径的合并节点
//        Map<T, Node> mergedMapping = new HashMap<>();
//
//        // 1. 找串行路径并合并
//        List<List<T>> serialPaths = findSerialPaths();
//
//        for (List<T> nodes : serialPaths) {
//            Node mergedNode = buildNodes(nodes);
//            optimized.addNode(mergedNode);
//            for (T nodeId : nodes) {
//                mergedMapping.put(nodeId, mergedNode);
//            }
//        }
//
//        // 2. 加入未合并的节点
//        for (T node : originalDag.getAllNodes()) {
//            if (!mergedMapping.containsKey(node)) {
//                optimized.addNode(buildNode(node));
//            }
//        }
//
//        // 3. 重新连边
//        for (T node : originalDag.getAllNodes()) {
//            Node fromNode = mergedMapping.getOrDefault(node, buildNode(node));
//            for (DAG.Edge<T> edge : originalDag.getOutgoingEdges(node)) {
//
//                Node toNode = mergedMapping.getOrDefault(edge.getTarget(), buildNode(edge.getTarget()));
//                if (!fromNode.equals(toNode)) {
//                    optimized.addEdge(fromNode, toNode, edge.getData());
//                }
//            }
//        }
//        return optimized;
//    }
//
//    private Node buildNode(T node) {
//        String nodeId = node.toString();
//        return new Node(nodeId, components.get(nodeId));
//    }
//
//    private Node buildNodes(List<T> nodes) {
//        List<String> nodeIds = nodes.stream().map(Object::toString).toList();
//        PipelineComponent<FlowCtx, ?> containerComponent = new PipelineComponent<>(Arrays.toString(nodeIds.toArray()));
//        nodeIds.forEach(nodeId -> {
//            IComponent<FlowCtx, ?> component = components.get(nodeId);
//            containerComponent.addComponent(component);
//        });
//        return new Node(nodeIds, containerComponent);
//    }
//
//    /**
//     * 找串行路径并合并
//     */
//    private List<List<T>> findSerialPaths() {
//        List<List<T>> paths = new ArrayList<>();
//        Set<T> visited = new HashSet<>();
//        // 获取拓扑排序的节点顺序
//        List<T> sortedNodes = originalDag.topologicalSortKahn();
//        // 去掉开始阶段
//        if (sortedNodes.size() > 2) {
//            sortedNodes = sortedNodes.subList(1, sortedNodes.size());
//        } else {
//            sortedNodes = Collections.emptyList();
//        }
//        for (T node : sortedNodes) {
//            if (visited.contains(node)) continue;
//            List<T> path = new ArrayList<>();
//            T current = node;
//            // 判断当前节点是否满足作为串行路径一部分的条件
//            while (current != null && !visited.contains(current)) {
//                path.add(current);
//                visited.add(current);
//
//                current = getSingleSuccessor(current);
//            }
//            // 如果路径长度大于1，就认为找到了一个串行路径
//            if (path.size() > 1){
//                paths.add(path);
//            }
//        }
//        return paths;
//    }
//
//    private T getSingleSuccessor(T node) {
//        // 检查节点是否有唯一的后继，且该后继有唯一的前驱
//        Set<T> successors = originalDag.getSuccessors(node);
//        if (successors.size() != 1) return null;
//
//        // 检查后继节点是否只有当前节点作为前驱
//        T next = successors.iterator().next();
//        // 如果是end节点，则返回null
//        if(next.equals(Constants.END_NODE)){
//            return null;
//        }
//        return originalDag.getPredecessors(next).size() == 1 ? next : null;
//    }
//}
