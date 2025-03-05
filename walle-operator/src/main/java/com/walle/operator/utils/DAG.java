package com.walle.operator.utils;


import com.walle.operator.common.constants.Constants;
import com.walle.operator.node.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个轻量的有向有权无环图结构
 * @author harley.shi
 * @date 2024/11/20
 */
public class DAG {

    /**
     * 使用邻接表表示图，Map<节点, 出边>
     */
    private final Map<Node, Set<Edge>> adjacencyList;

    /**
     * 存储节点的入度，Map<节点, 入度>
     */
    private final Map<Node, Integer> inDegrees;

    /**
     * 节点依赖的边，Map<节点, 入边>
     */
    private final Map<Node, Set<Edge>> reverseEdgesMap;

    public DAG() {
        this.adjacencyList = new HashMap<>();
        this.inDegrees = new HashMap<>();
        this.reverseEdgesMap = new HashMap<>();
    }

    /**
     * 添加节点
     * @param node 节点
     */
    public void addNode(Node node) {
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
    public void addEdge(Node from, Node to) {
        addEdge(from, to, null);
    }

    /**
     * 添加边（避免重复边）
     * @param from 源节点
     * @param to 目标节点
     * @param data 边的信息，比如权重、路径等其他信息
     */
    public void addEdge(Node from, Node to, Object data) {
        addNode(from);
        addNode(to);
        Edge edge = new Edge(to, data);
        if(adjacencyList.get(from).contains(edge)){
            return;
        }
        adjacencyList.get(from).add(edge);
        reverseEdgesMap.get(to).add(new Edge(from, null));
        inDegrees.put(to, inDegrees.getOrDefault(to, 0) + 1);
    }

    /**
     * 获取所有入度为0的节点列表
     * @return 入度为0的节点列表
     */
    public List<Node> getNodesWithZeroInDegree() {
        List<Node> nodesWithZeroInDegree = new ArrayList<>();
        for (Map.Entry<Node, Integer> entry : inDegrees.entrySet()) {
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
    public Set<Edge> getOutgoingEdges(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptySet());
    }

    /**
     * 获取节点的所有后继节点
     */
    public Set<Node> getSuccessors(Node node) {
        Set<Node> successors = new HashSet<>();
        for (Edge edge : getOutgoingEdges(node)) {
            successors.add(edge.getTarget());
        }
        return successors;
    }

    /**
     * 获取节点的所有前驱节点
     */
    public Set<Node> getPredecessors(Node node) {
        Set<Node> predecessors = new HashSet<>();
        for (Edge edge : reverseEdgesMap.get(node)) {
            predecessors.add(edge.getTarget());
        }
        return predecessors;
    }


    /**
     * 获取图中的所有节点
     * @return 所有节点的集合
     */
    public Set<Node> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * 复制一份入度表
     */
    public Map<Node, AtomicInteger> copyInDegrees(){
        Map<Node, AtomicInteger> inDegreesCopy = new ConcurrentHashMap<>();
        this.inDegrees.forEach((key, value)-> inDegreesCopy.put(key, new AtomicInteger(value)));
        return inDegreesCopy;
    }

    /**
     * 使用Kahn算法进行拓扑排序
     */
    public List<Node> topologicalSortKahn() {
        Map<Node, Integer> inDegree = new HashMap<>();

        // 1. 计算每个节点的入度
        for (Node node : getAllNodes()) {
            inDegree.put(node, 0);
        }
        for (Node node : getAllNodes()) {
            for (Node successor : getSuccessors(node)) {
                inDegree.put(successor, inDegree.getOrDefault(successor, 0) + 1);
            }
        }

        Queue<Node> queue = new LinkedList<>();
        // 2. 将入度为零的节点加入队列
        for (Node node : getAllNodes()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        List<Node> sortedNodes = new ArrayList<>();
        // 3. 开始遍历并构建拓扑排序
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            sortedNodes.add(node);

            // 对该节点的后继节点更新入度
            for (Node successor : getSuccessors(node)) {
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
    public static class Edge {
        /**
         * 目标节点
         */
        Node target;

        /**
         * 边的信息，比如权重、路径等其他信息
         */
        Object data;

        public Edge(Node target, Object data) {
            this.target = target;
            this.data = data;
        }

        public Node getTarget() {
            return target;
        }

        public Object getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
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

    /**
     * 打印图的结构，效果类似如下格式
     */
    public void printGraph() {
        List<Node> roots = getNodesWithZeroInDegree();
        // 对于每个入度为 0 的节点，递归打印其子树
        for (int i = 0; i < roots.size(); i++) {
            printGraphRecursive(roots.get(i), "", i == roots.size() - 1);
        }
    }

    /**
     * 递归打印每个节点及其出边，构造漂亮的树形结构
     * @param prefix 当前行的前缀（包含空格及连线）
     * @param isTail 当前节点是否为同级的最后一个
     */
    private void printGraphRecursive(Node node, String prefix, boolean isTail) {
        // 打印当前节点，采用方括号格式，如 [Node 1]
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + node + "]");
        // 获取当前节点的所有出边
        Set<Edge> children = getOutgoingEdges(node);
        List<Edge> childrenList = new ArrayList<>(children);
        // 对于每个子节点，递归调用打印方法
        for (int i = 0; i < childrenList.size(); i++) {
            Node child = childrenList.get(i).getTarget();
            // 是否为最后一个子节点
            boolean last = i == childrenList.size() - 1;
            // 更新前缀，最后一个节点时使用空格，否则使用竖线标记
            printGraphRecursive(child, prefix + (isTail ? "    " : "│   "), last);
        }
    }




    public static void main(String[] args) {
        DAG graph = new DAG();
        graph.addEdge(new Node("run"), new Node("start"));
        graph.addEdge(new Node("start"), new Node("hot_recall"));
        graph.addEdge(new Node("start"), new Node("user_cf_recall"));
        graph.addEdge(new Node("start"), new Node("i2i_recall"));
        graph.addEdge(new Node("hot_recall"), new Node("merge_recall"));
        graph.addEdge(new Node("user_cf_recall"), new Node("merge_recall"));
        graph.addEdge(new Node("i2i_recall"), new Node("merge_recall"));
        graph.addEdge(new Node("merge_recall"), new Node("ctr_rank"));
        graph.addEdge(new Node("ctr_rank"), new Node("top_rerank"));
        graph.addEdge(new Node("top_rerank"), new Node("end"));
        graph.addEdge(new Node("end"), Constants.END_NODE);

//        graph.addEdge("1", "2");
//        graph.addEdge("1", "3");
//        graph.addEdge("3", "4");
//        graph.addEdge("4", "5");
//        graph.addEdge("5", "9");
//        graph.addEdge("2", "6");
//        graph.addEdge("6", "7");
//        graph.addEdge("7", "9");
//        graph.addEdge("6", "8");
//        graph.addEdge("8", "9");

//        graph.addEdge(new SimpleNode<>("1"), new SimpleNode<>("2"));
//        graph.addEdge(new SimpleNode<>("1"), new SimpleNode<>("3"));
//        graph.addEdge(new SimpleNode<>("3"), new SimpleNode<>("4"));
//        graph.addEdge(new SimpleNode<>("4"), new SimpleNode<>("5"));
//        graph.addEdge(new SimpleNode<>("5"), new SimpleNode<>("9"));
//        graph.addEdge(new SimpleNode<>("2"), new SimpleNode<>("6"));
//        graph.addEdge(new SimpleNode<>("6"), new SimpleNode<>("7"));
//        graph.addEdge(new SimpleNode<>("7"), new SimpleNode<>("9"));
//        graph.addEdge(new SimpleNode<>("6"), new SimpleNode<>("8"));
//        graph.addEdge(new SimpleNode<>("8"), new SimpleNode<>("9"));



        graph.printGraph();

        System.out.println();
        // 优化DAG
        DAGOptimizer optimizer = new DAGOptimizer(graph);
        DAG optimizedGraph = optimizer.optimize();
        optimizedGraph.printGraph();

//        System.out.println();
//        // 优化DAG
//        DAGOptimizerV1<String> optimizerV1 = new DAGOptimizerV1<>(graph);
//        DAG<String> optimizedGraphV1 = optimizerV1.optimize();
//        optimizedGraphV1.printGraph();

    }
}


