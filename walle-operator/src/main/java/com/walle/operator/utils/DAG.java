package com.walle.operator.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个轻量的有向有权无环图结构
 * @author harley.shi
 * @date 2024/11/20
 */
public class DAG<T> {

    /**
     * 使用邻接表表示图，Map<节点, 出边>
     */
    private final Map<T, Set<Edge<T>>> adjacencyList;

    /**
     * 存储节点的入度，Map<节点, 入度>
     */
    private final Map<T, Integer> inDegrees;

    /**
     * 节点依赖的边，Map<节点, 入边>
     */
    private final Map<T, Set<Edge<T>>> reverseEdgesMap;

    public DAG() {
        this.adjacencyList = new HashMap<>();
        this.inDegrees = new HashMap<>();
        this.reverseEdgesMap = new HashMap<>();
    }

    /**
     * 添加节点
     * @param node 节点
     */
    public void addNode(T node) {
        if(adjacencyList.containsKey(node)){
            return;
        }
        adjacencyList.put(node, new HashSet<>());
        reverseEdgesMap.put(node, new HashSet<>());
        // 入度初始化为 0
        inDegrees.put(node, 0);
    }

    /**
     * 添加边
     * @param from 源节点
     * @param to 目标节点
     */
    public void addEdge(T from, T to) {
        addEdge(from, to, null);
    }

    /**
     * 添加边（避免重复边）
     * @param from 源节点
     * @param to 目标节点
     * @param data 边的信息，比如权重、路径等其他信息
     */
    public void addEdge(T from, T to, Object data) {
        addNode(from);
        addNode(to);
        Edge<T> edge = new Edge<>(to, data);
        if(adjacencyList.get(from).contains(edge)){
            return;
        }
        adjacencyList.get(from).add(edge);
        reverseEdgesMap.get(to).add(new Edge<>(from, null));
        inDegrees.put(to, inDegrees.getOrDefault(to, 0) + 1);
    }

    /**
     * 获取所有入度为0的节点列表
     * @return 入度为0的节点列表
     */
    public List<T> getNodesWithZeroInDegree() {
        List<T> nodesWithZeroInDegree = new ArrayList<>();
        for (Map.Entry<T, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                nodesWithZeroInDegree.add(entry.getKey());
            }
        }
        return nodesWithZeroInDegree;
    }

    /**
     * 获取节点的所有邻接节点（出边）
     * @param node 节点
     * @return 邻接节点的集合
     */
    public Set<Edge<T>> getOutgoingEdges(T node) {
        return adjacencyList.getOrDefault(node, Collections.emptySet());
    }

    /**
     * 获取节点的所有后继节点
     */
    public Set<T> getSuccessors(T node) {
        Set<T> successors = new HashSet<>();
        for (Edge<T> edge : getOutgoingEdges(node)) {
            successors.add(edge.getTarget());
        }
        return successors;
    }

    /**
     * 获取节点的所有前驱节点
     */
    public Set<T> getPredecessors(T node) {
        Set<T> predecessors = new HashSet<>();
        for (Edge<T> edge : reverseEdgesMap.get(node)) {
            predecessors.add(edge.getTarget());
        }
        return predecessors;
    }


    /**
     * 获取图中的所有节点
     * @return 所有节点的集合
     */
    public Set<T> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * 复制一份入度表
     */
    public Map<T, AtomicInteger> copyInDegrees(){
        Map<T, AtomicInteger> inDegreesCopy = new ConcurrentHashMap<>();
        this.inDegrees.forEach((key, value)-> inDegreesCopy.put(key, new AtomicInteger(value)));
        return inDegreesCopy;
    }

    /**
     * 使用Kahn算法进行拓扑排序
     */
    public List<T> topologicalSortKahn() {
        Map<T, Integer> inDegree = new HashMap<>();

        // 1. 计算每个节点的入度
        for (T node : getAllNodes()) {
            inDegree.put(node, 0);
        }
        for (T node : getAllNodes()) {
            for (T successor : getSuccessors(node)) {
                inDegree.put(successor, inDegree.getOrDefault(successor, 0) + 1);
            }
        }

        Queue<T> queue = new LinkedList<>();
        // 2. 将入度为零的节点加入队列
        for (T node : getAllNodes()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        List<T> sortedNodes = new ArrayList<>();
        // 3. 开始遍历并构建拓扑排序
        while (!queue.isEmpty()) {
            T node = queue.poll();
            sortedNodes.add(node);

            // 对该节点的后继节点更新入度
            for (T successor : getSuccessors(node)) {
                inDegree.put(successor, inDegree.get(successor) - 1);
                if (inDegree.get(successor) == 0) {
                    queue.offer(successor);
                }
            }
        }

        // 4. 检查是否有环
        if (sortedNodes.size() != getAllNodes().size()) {
            throw new IllegalStateException("Cycle detected in the graph!");
        }
        return sortedNodes;
    }

    /**
     * 边
     */
    public static class Edge<T> {
        /**
         * 目标节点
         */
        T target;

        /**
         * 边的信息，比如权重、路径等其他信息
         */
        Object data;

        public Edge(T target, Object data) {
            this.target = target;
            this.data = data;
        }

        public T getTarget() {
            return target;
        }

        public Object getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<T> edge = (Edge<T>) o;
            return Objects.equals(target, edge.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(target);
        }

        @Override
        public String toString() {
            return target.toString() + (data != null ? "(" + data + ")" : "");
        }
    }
}


