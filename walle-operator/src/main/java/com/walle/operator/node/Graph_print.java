//package com.walle.operator.node;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 自定义一个轻量的有向有权无环图结构
// * @author harley.shi
// * @date 2024/11/20
// */
//public class Graph_print<T> {
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
//    public Graph_print() {
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
//     * 获取图中的所有节点
//     * @return 所有节点的集合
//     */
//    public Set<T> getAllNodes() {
//        return adjacencyList.keySet();
//    }
//
//
//    public Map<T, Integer> copySimpleInDegrees(){
//        return new HashMap<>(inDegrees);
//    }
//
//    /**
//     * 复制一份入度表
//     */
//    public Map<T, AtomicInteger> copyInDegrees(){
//        Map<T, AtomicInteger> inDegreesCopy = new ConcurrentHashMap<>();
//        this.inDegrees.forEach((key, value)-> inDegreesCopy.put(key, new AtomicInteger(value)));
//        return inDegreesCopy;
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
//
//    /**
//     * 打印图的结构，效果类似如下格式
//     */
//    public void printGraph() {
//        List<T> roots = getNodesWithZeroInDegree();
//        // 对于每个入度为 0 的节点，递归打印其子树
//        for (int i = 0; i < roots.size(); i++) {
//            printGraphRecursive(roots.get(i), "", i == roots.size() - 1);
//        }
//    }
//
//    /**
//     * 递归打印每个节点及其出边，构造漂亮的树形结构
//     * @param prefix 当前行的前缀（包含空格及连线）
//     * @param isTail 当前节点是否为同级的最后一个
//     */
//    private void printGraphRecursive(T node, String prefix, boolean isTail) {
//        // 打印当前节点，采用方括号格式，如 [Node 1]
//        System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + node + "]");
//        // 获取当前节点的所有出边
//        Set<Edge<T>> children = getNeighbors(node);
//        List<Edge<T>> childrenList = new ArrayList<>(children);
//        // 对于每个子节点，递归调用打印方法
//        for (int i = 0; i < childrenList.size(); i++) {
//            T child = childrenList.get(i).getTarget();
//            // 是否为最后一个子节点
//            boolean last = i == childrenList.size() - 1;
//            // 更新前缀，最后一个节点时使用空格，否则使用竖线标记
//            printGraphRecursive(child, prefix + (isTail ? "    " : "│   "), last);
//        }
//    }
//
//
//
//    public static void main(String[] args) {
//        Graph_print<Node<String>> graph = new Graph_print<>();
//        graph.addEdge(new PipelineNode<>("1"), new PipelineNode<>("2"));
//        graph.addEdge(new PipelineNode<>("1"), new PipelineNode<>("3"));
//        graph.addEdge(new PipelineNode<>("3"), new PipelineNode<>("4"));
//        graph.addEdge(new PipelineNode<>("4"), new PipelineNode<>("5"));
//        graph.addEdge(new PipelineNode<>("5"), new PipelineNode<>("9"));
//        graph.addEdge(new PipelineNode<>("2"), new PipelineNode<>("6"));
//        graph.addEdge(new PipelineNode<>("6"), new PipelineNode<>("7"));
//        graph.addEdge(new PipelineNode<>("7"), new PipelineNode<>("9"));
//        graph.addEdge(new PipelineNode<>("6"), new PipelineNode<>("8"));
//        graph.addEdge(new PipelineNode<>("8"), new PipelineNode<>("9"));
//
//        graph.printGraph();
//    }
//}
