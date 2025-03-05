//package com.walle.operator.utils;
//
//import java.util.*;
//
///**
// * 图优化器：合并串行路径
// * @author harley.shi
// * @date 2025/1/5
// */
//public class DAGOptimizerV1<T> {
//    private final DAG<T> originalGraph;
//
//    public DAGOptimizerV1(DAG<T> graph) {
//        this.originalGraph = graph;
//    }
//
//    public DAG<T> optimize() {
//        DAG<T> optimized = new DAG<>();
//
//        // 构建原始节点到合并节点的映射：如果节点属于某条串行路径，则映射到该路径的合并节点
//        Map<T, T> mergedMapping = new HashMap<>();
//
//        // 1. 找串行路径并合并
//        List<List<T>> serialPaths = findSerialPaths();
//
//        for (List<T> path : serialPaths) {
//            T mergedNode = mergeNodes(path);
//            optimized.addNode(mergedNode);
//            for (T node : path) {
//                mergedMapping.put(node, mergedNode);
//            }
//        }
//
//        // 2. 加入未合并的节点
//        for (T node : originalGraph.getAllNodes()) {
//            if (!mergedMapping.containsKey(node)) {
//                optimized.addNode(node);
//            }
//        }
//
//        // 3. 重新连边
//        for (T node : originalGraph.getAllNodes()) {
//            T from = mergedMapping.getOrDefault(node, node);
//            for (DAG.Edge<T> edge : originalGraph.getOutgoingEdges(node)) {
//                T to = mergedMapping.getOrDefault(edge.getTarget(), edge.getTarget());
//                if (!from.equals(to)) {
//                    optimized.addEdge(from, to, edge.getData());
//                }
//            }
//        }
//        return optimized;
//    }
//
//    /**
//     * 找串行路径并合并
//     */
//    private List<List<T>> findSerialPaths() {
//        List<List<T>> paths = new ArrayList<>();
//        Set<T> visited = new HashSet<>();
//        // 获取拓扑排序的节点顺序
//        List<T> sortedNodes = originalGraph.topologicalSortKahn();
//        for (T node : sortedNodes) {
//            if (visited.contains(node)) continue;
//
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
//        Set<T> successors = originalGraph.getSuccessors(node);
//        if (successors.size() != 1) return null;
//
//        // 检查后继节点是否只有当前节点作为前驱
//        T next = successors.iterator().next();
//        return originalGraph.getPredecessors(next).size() == 1 ? next : null;
//    }
//
//
//    @SuppressWarnings("unchecked")
//    private T mergeNodes(List<T> path) {
//        return (T) new ArrayList<>(path);  // 注意实际项目中按 T 类型定义合并规则
//    }
//}
