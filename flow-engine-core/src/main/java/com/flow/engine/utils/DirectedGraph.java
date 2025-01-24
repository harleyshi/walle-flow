package com.flow.engine.utils;

import com.alibaba.fastjson2.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个轻量的有向有权无环图结构
 * @author harley.shi
 * @date 2024/11/20
 */
public class DirectedGraph<T> {

    /**
     * 使用邻接表表示图，避免重复的边
     */
    private final Map<T, Set<Edge<T>>> adjacencyList;

    /**
     * 存储节点的入度，Map<节点, 入度>
     */
    private final Map<T, Integer> inDegrees;

    public DirectedGraph() {
        this.adjacencyList = new HashMap<>();
        this.inDegrees = new HashMap<>();
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
    public Set<Edge<T>> getNeighbors(T node) {
        return adjacencyList.getOrDefault(node, Collections.emptySet());
    }

    /**
     * 获取节点的出边数量
     * @param node 节点
     * @return 节点的出边数量
     */
    public int getOutDegree(T node) {
        Set<Edge<T>> neighbors = getNeighbors(node);
        return neighbors == null ? 0 : neighbors.size();
    }

    /**
     * 获取节点的入边数量
     * @param node 节点
     * @return 节点的入边数量
     */
    public int getInDegree(T node) {
        return inDegrees.getOrDefault(node, 0);
    }

    /**
     * 打印图的邻接表
     */
    public void printGraph() {
        for (Map.Entry<T, Set<Edge<T>>> entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    /**
     * 获取图中的所有节点
     * @return 所有节点的集合
     */
    public Set<T> getAllNodes() {
        return adjacencyList.keySet();
    }


    public Map<T, Integer> copySimpleInDegrees(){
        return new HashMap<>(inDegrees);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<?> edge = (Edge<?>) o;
            return Objects.equals(target, edge.target);
        }

        public T getTarget() {
            return target;
        }

        public Object getData() {
            return data;
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


    /**
     * 分层拓扑排序
     * 将拓扑排序的结果转换为层级结构，每一层中的节点可以并行执行
     * @return 分层拓扑排序的结果，每一层是一个列表
     */
    public List<List<T>> topologicalSort() {
        List<List<T>> result = new ArrayList<>();
        Map<T, Integer> inDegreeCopy = new HashMap<>(inDegrees);
        Queue<T> queue = new LinkedList<>(getNodesWithZeroInDegree());

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<T> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                T node = queue.poll();
                currentLevel.add(node);

                // 处理邻接节点
                Set<Edge<T>> neighbors = getNeighbors(node);
                for (Edge<T> edge : neighbors) {
                    T target = edge.getTarget();
                    // 更新入度并将入度为 0 的节点加入队列
                    inDegreeCopy.put(target, inDegreeCopy.get(target) - 1);
                    if (inDegreeCopy.get(target) == 0) {
                        queue.offer(target);
                    }
                }
            }

            result.add(currentLevel);
        }

        return result;
    }

    public static void main(String[] args) {
        DirectedGraph<String> graph = new DirectedGraph<>();

        // 添加边，确保不会有重复的边
        graph.addEdge("0", "1");
        graph.addEdge("0", "2");
        graph.addEdge("0", "3");
        graph.addEdge("1", "4");
        graph.addEdge("4", "5");
        graph.addEdge("5", "6");
        graph.addEdge("2", "7");
        graph.addEdge("7", "6");
        graph.addEdge("3", "6");
        graph.addEdge("6", "9");

        // 打印图的邻接表
        graph.printGraph();

        System.out.println("拓扑排序结果 \n"+ JSONObject.toJSONString(graph.topologicalSort()));
    }

    static String str = """
                        
                        
                             
                        
            [
                [
                    {
                        "data": "0"
                    }
                ],
                [
                    {
                        "data": "1",
                        "next": {
                            "data": "4",
                            "next": {
                                "data": "5"
                            }
                        }
                    },
                    {
                        "data": "2",
                        "next": {
                            "data": "7"
                        }
                    },
                    {
                        "data": "3",
                        "next": {
                            
                        }
                    }
                ],
                [
                    {
                        "data": "6"
                    }
                ],
                [
                    {
                        "data": "9"
                    }
                ]
            ]
            """;


//    public static void main(String[] args) {
//        DirectedGraph<String> graph = new DirectedGraph<>();
//
//        // 添加节点和边
//        graph.addNode("S");
//        graph.addNode("A");
//        graph.addNode("B");
//        graph.addNode("C");
//        graph.addNode("D");
//
//        // 添加边，确保不会有重复的边
//        graph.addEdge("S", "B", "11");
//        graph.addEdge("A", "B", "10");
//        graph.addEdge("A", "C", "1");
//        graph.addEdge("B", "D");
//        graph.addEdge("C", "D");
//
//        // 打印图的邻接表
//        System.out.println("Graph:");
//        graph.printGraph();
//
//        // 获取某节点的出边数量和入边数量
//        System.out.println("Out degree of A: " + graph.getOutDegree("A"));
//        System.out.println("In degree of D: " + graph.getInDegree("D"));
//
//        // 获取入度为0的节点列表
//        List<String> nodesWithZeroInDegree = graph.getNodesWithZeroInDegree();
//        System.out.println("Nodes with zero in-degree: " + nodesWithZeroInDegree);
//    }
}


