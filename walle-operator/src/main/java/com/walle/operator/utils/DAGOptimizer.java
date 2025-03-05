package com.walle.operator.utils;

import com.walle.operator.common.constants.Constants;
import com.walle.operator.node.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图优化器：合并串行路径
 * @author harley.shi
 * @date 2025/1/5
 */
public class DAGOptimizer {
    private final DAG originalGraph;

    public DAGOptimizer(DAG graph) {
        this.originalGraph = graph;
    }

    public DAG optimize() {
        DAG optimized = new DAG();

        // 构建原始节点到合并节点的映射：如果节点属于某条串行路径，则映射到该路径的合并节点
        Map<Node, Node> mergedMapping = new HashMap<>();

        // 1. 找串行路径并合并
        List<List<Node>> serialPaths = findSerialPaths();

        for (List<Node> nodes : serialPaths) {
            List<String> nodeIds = nodes.stream().map(Node::getNodeId).collect(Collectors.toList());
            Node mergedNode = new Node(nodeIds);
            optimized.addNode(mergedNode);
            for (Node nodeId : nodes) {
                mergedMapping.put(nodeId, mergedNode);
            }
        }

        // 2. 加入未合并的节点
        for (Node node : originalGraph.getAllNodes()) {
            if (!mergedMapping.containsKey(node)) {
                optimized.addNode(node);
            }
        }

        // 3. 重新连边
        for (Node node : originalGraph.getAllNodes()) {
            Node fromNode = mergedMapping.getOrDefault(node, node);
            for (DAG.Edge edge : originalGraph.getOutgoingEdges(node)) {
                Node toNode = mergedMapping.getOrDefault(edge.getTarget(), edge.getTarget());
                if (!fromNode.equals(toNode)) {
                    optimized.addEdge(fromNode, toNode, edge.getData());
                }
            }
        }
        return optimized;
    }

    /**
     * 找串行路径并合并
     */
    private List<List<Node>> findSerialPaths() {
        List<List<Node>> paths = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        // 获取拓扑排序的节点顺序
        List<Node> sortedNodes = originalGraph.topologicalSortKahn();
        // 去掉开始阶段
        if (sortedNodes.size() > 2) {
            sortedNodes = sortedNodes.subList(1, sortedNodes.size());
        } else {
            sortedNodes = Collections.emptyList();
        }
        for (Node node : sortedNodes) {
            if (visited.contains(node)) continue;

            List<Node> path = new ArrayList<>();
            Node current = node;
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

    private Node getSingleSuccessor(Node node) {
        // 检查节点是否有唯一的后继，且该后继有唯一的前驱
        Set<Node> successors = originalGraph.getSuccessors(node);
        if (successors.size() != 1) return null;

        // 检查后继节点是否只有当前节点作为前驱
        Node next = successors.iterator().next();
        // 如果是end节点，则返回null
        if(next.equals(Constants.END_NODE)){
            return null;
        }
        return originalGraph.getPredecessors(next).size() == 1 ? next : null;
    }
}
