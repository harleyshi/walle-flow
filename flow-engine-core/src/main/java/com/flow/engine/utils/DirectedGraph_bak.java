//package com.flow.engine.utils;
//
//import com.flow.engine.parser.node.Node;
//
//import java.util.*;
//
///**
// * 自定义一个轻量的有向有权无环图结构
// * @author harley.shi
// * @date 2024/11/20
// */
//public class DirectedGraph_bak<T> {
//
//    /**
//     * 使用邻接表表示图，避免重复的边
//     */
//    private final Map<T, Set<Edge<T>>> adjacencyList;
//
//    /**
//     * 存储节点的入度，Map<节点, 入度>
//     */
//    private final Map<T, Integer> inDegrees;
//
//    public DirectedGraph_bak() {
//        this.adjacencyList = new HashMap<>();
//        this.inDegrees = new HashMap<>();
//    }
//
//    /**
//     * 添加节点
//     * @param node 节点
//     */
//    public void addNode(T node) {
//        if(adjacencyList.containsKey(node)){
//            return;
//        }
//        adjacencyList.put(node, new HashSet<>());
//        // 入度初始化为 0
//        inDegrees.put(node, 0);
//    }
//
//    /**
//     * 添加边
//     * @param from 源节点
//     * @param to 目标节点
//     */
//    public void addEdge(T from, T to) {
//        addEdge(from, to, null);
//    }
//
//    /**
//     * 添加边（避免重复边）
//     * @param from 源节点
//     * @param to 目标节点
//     * @param data 边的信息，比如权重、路径等其他信息
//     */
//    public void addEdge(T from, T to, Object data) {
//        addNode(from);
//        addNode(to);
//        Edge<T> edge = new Edge<>(to, data);
//        if(adjacencyList.get(from).contains(edge)){
//            return;
//        }
//        adjacencyList.get(from).add(edge);
//        inDegrees.put(to, inDegrees.getOrDefault(to, 0) + 1);
//    }
//
//    /**
//     * 获取所有入度为0的节点列表
//     * @return 入度为0的节点列表
//     */
//    public List<T> getNodesWithZeroInDegree() {
//        List<T> nodesWithZeroInDegree = new ArrayList<>();
//        for (Map.Entry<T, Integer> entry : inDegrees.entrySet()) {
//            if (entry.getValue() == 0) {
//                nodesWithZeroInDegree.add(entry.getKey());
//            }
//        }
//        return nodesWithZeroInDegree;
//    }
//
//    /**
//     * 获取节点的所有邻接节点（出边）
//     * @param node 节点
//     * @return 邻接节点的集合
//     */
//    public Set<Edge<T>> getNeighbors(T node) {
//        return adjacencyList.getOrDefault(node, Collections.emptySet());
//    }
//
//    /**
//     * 获取节点的出边数量
//     * @param node 节点
//     * @return 节点的出边数量
//     */
//    public int getOutDegree(T node) {
//        Set<Edge<T>> neighbors = getNeighbors(node);
//        return neighbors == null ? 0 : neighbors.size();
//    }
//
//    /**
//     * 获取节点的入边数量
//     * @param node 节点
//     * @return 节点的入边数量
//     */
//    public int getInDegree(T node) {
//        return inDegrees.getOrDefault(node, 0);
//    }
//
//    /**
//     * 打印图的邻接表
//     */
//    public void printGraph() {
//        for (Map.Entry<T, Set<Edge<T>>> entry : adjacencyList.entrySet()) {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        }
//    }
//
//    /**
//     * 获取图中的所有节点
//     * @return 所有节点的集合
//     */
//    public Set<T> getAllNodes() {
//        return adjacencyList.keySet();
//    }
//
//    /**
//     * 复制一份入度表
//     */
//    public Map<T, Integer> copyInDegrees() {
//        return new HashMap<>(this.inDegrees);
//    }
//
//    /**
//     * 分层拓扑排序
//     * 将拓扑排序的结果转换为层级结构，每一层中的节点可以并行执行
//     * @return 分层拓扑排序的结果，每一层是一个列表
//     */
//    public List<Node<T>> topologicalSortAsLinkedList() {
//        // 结果列表，存储拓扑排序的结果
//        List<Node<T>> sortedNodes = new ArrayList<>();
//        // 入度表，存储每个节点的当前入度
//        Map<T, Integer> inDegreeMap = new HashMap<>(this.inDegrees);
//        // 队列，用于存储入度为 0 的节点
//        Queue<T> queue = new LinkedList<>(this.getNodesWithZeroInDegree());
//
//        // 拓扑排序过程
//        while (!queue.isEmpty()) {
//            // 取出一个入度为 0 的节点
//            T nodeData = queue.poll();
//            // 创建节点对象
//            Node<T> node = new Node<>(nodeData);
//            // 将该节点加入排序结果
//            sortedNodes.add(node);
//
//            // 遍历该节点的邻接节点
//            for (Edge<T> edge : this.getNeighbors(nodeData)) {
//                T neighbor = edge.getTarget();
//                // 减少邻接节点的入度
//                inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) - 1);
//
//                // 如果邻接节点的入度变为 0，加入队列
//                if (inDegreeMap.get(neighbor) == 0) {
//                    queue.add(neighbor);
//                }
//            }
//        }
//        // 检查是否有环
//        if (sortedNodes.size() != this.getAllNodes().size()) {
//            throw new IllegalStateException("图中存在环，无法进行拓扑排序");
//        }
//        return sortedNodes;
//    }
//
//
//    public List<T> layeredTopologicalSort() {
//        // 结果列表，存储每一层的节点
//        List<T> list = new ArrayList<>();
//        // 入度表
//        Map<T, Integer> inDegreeMap = new HashMap<>(this.inDegrees);
//        // 队列，用于存储当前层的节点
//        Queue<T> queue = new LinkedList<>(this.getNodesWithZeroInDegree());
//
//        // 分层拓扑排序
//        while (!queue.isEmpty()) {
//            T node = queue.poll();
//            // 减少邻接节点的入度
//            for (DirectedGraph.Edge<T> edge : this.getNeighbors(node)) {
//                T targetNode = edge.getTarget();
//                inDegreeMap.put(targetNode, inDegreeMap.get(targetNode) - 1);
//
//                // 如果邻接节点的入度为 0，加入队列
//                if (inDegreeMap.get(targetNode) == 0) {
//                    queue.add(targetNode);
//                }
//            }
//            list.add(node);
//        }
//
//        // 检查是否有环
//        for (Map.Entry<T, Integer> entry : inDegreeMap.entrySet()) {
//            if (entry.getValue() > 0) {
//                throw new IllegalStateException("图中存在环，无法进行分层拓扑排序");
//            }
//        }
//
//        return list;
//    }
//
//    public List<List<T>> layeredTopologicalSort() {
//        // 结果列表，存储每一层的节点
//        List<List<T>> layers = new ArrayList<>();
//        // 入度表
//        Map<T, Integer> inDegreeMap = new HashMap<>(this.inDegrees);
//        // 队列，用于存储当前层的节点
//        Queue<T> queue = new LinkedList<>(this.getNodesWithZeroInDegree());
//
//        // 分层拓扑排序
//        while (!queue.isEmpty()) {
//            // 当前层的节点
//            List<T> currentLayer = new ArrayList<>();
//            int levelSize = queue.size();
//            for (int i = 0; i < levelSize; i++) {
//                T node = queue.poll();
//                currentLayer.add(node);
//
//                // 减少邻接节点的入度
//                for (Edge<T> edge : this.getNeighbors(node)) {
//                    T neighbor = edge.getTarget();
//                    inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) - 1);
//
//                    // 如果邻接节点的入度为 0，加入队列
//                    if (inDegreeMap.get(neighbor) == 0) {
//                        queue.add(neighbor);
//                    }
//                }
//            }
//            layers.add(currentLayer);
//        }
//
//        // 检查是否有环
//        for (Map.Entry<T, Integer> entry : inDegreeMap.entrySet()) {
//            if (entry.getValue() > 0) {
//                throw new IllegalStateException("图中存在环，无法进行分层拓扑排序");
//            }
//        }
//
//        return layers;
//    }
//
//    /**
//     * 边
//     */
//    public static class Edge<T> {
//        /**
//         * 目标节点
//         */
//        T target;
//
//        /**
//         * 边的信息，比如权重、路径等其他信息
//         */
//        Object data;
//
//        public Edge(T target, Object data) {
//            this.target = target;
//            this.data = data;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            Edge<?> edge = (Edge<?>) o;
//            return Objects.equals(target, edge.target);
//        }
//
//        public T getTarget() {
//            return target;
//        }
//
//        public Object getData() {
//            return data;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(target);
//        }
//
//        @Override
//        public String toString() {
//            return target.toString() + (data != null ? "(" + data + ")" : "");
//        }
//    }
//
//    public static void main(String[] args) {
//        DirectedGraph_bak<String> graph = new DirectedGraph_bak<>();
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
//}
//
//
