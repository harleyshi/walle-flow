package com.walle.operator.node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个轻量的有向有权无环图结构
 * @author harley.shi
 * @date 2024/11/20
 */
public class Graph<T> {
    /**
     * 使用邻接表表示图，避免重复的边
     */
    private final Map<T, Set<Edge<T>>> adjacencyList;

    /**
     * 存储节点的入度，Map<节点, 入度>
     */
    private final Map<T, Integer> inDegrees;

    public Graph() {
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
    public Set<Edge<T>> getOutgoingEdges(T node) {
        return adjacencyList.getOrDefault(node, Collections.emptySet());
    }

    /**
     * 获取节点的出边数量
     * @param node 节点
     * @return 节点的出边数量
     */
    public int getOutDegree(T node) {
        Set<Edge<T>> neighbors = getOutgoingEdges(node);
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
        for (Map.Entry<T, Set<Edge<T>>> entry : adjacencyList.entrySet()) {
            for (Edge<T> edge : entry.getValue()) {
                if (edge.getTarget().equals(node)) {
                    predecessors.add(entry.getKey());
                }
            }
        }
        return predecessors;
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<?> edge = (Edge<?>) o;
            return Objects.equals(target, edge.target);
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
        List<T> roots = getNodesWithZeroInDegree();
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
    private void printGraphRecursive(T node, String prefix, boolean isTail) {
        // 打印当前节点，采用方括号格式，如 [Node 1]
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + node + "]");
        // 获取当前节点的所有出边
        Set<Edge<T>> children = getOutgoingEdges(node);
        List<Edge<T>> childrenList = new ArrayList<>(children);
        // 对于每个子节点，递归调用打印方法
        for (int i = 0; i < childrenList.size(); i++) {
            T child = childrenList.get(i).getTarget();
            // 是否为最后一个子节点
            boolean last = i == childrenList.size() - 1;
            // 更新前缀，最后一个节点时使用空格，否则使用竖线标记
            printGraphRecursive(child, prefix + (isTail ? "    " : "│   "), last);
        }
    }

    /**
     * 图优化器：合并串行路径
     */
    public static class Optimizer<T> {

        private final Graph<T> originalGraph;
        private final Map<List<T>, T> pathToMergedNodeMap = new HashMap<>();
        private final Map<T, List<T>> mergedNodeToPathMap = new HashMap<>();

        public Optimizer(Graph<T> graph) {
            this.originalGraph = graph;
        }

        /**
         * 执行图优化
         */
        public Graph<T> optimize() {
            // 创建新图
            Graph<T> optimizedGraph = new Graph<>();

            // 找出所有串行路径
            List<List<T>> serialPaths = findSerialPaths();

            // 打印找到的串行路径（调试用）
            System.out.println("找到的最长串行路径:");
            for (List<T> path : serialPaths) {
                System.out.println(path);
            }
            System.out.println();

            // 构建优化后的图
            buildOptimizedGraph(optimizedGraph, serialPaths);

            return optimizedGraph;
        }

        /**
         * 查找所有的串行路径
         */
        private List<List<T>> findSerialPaths() {
            List<List<T>> serialPaths = new ArrayList<>();
            Set<T> processedNodes = new HashSet<>();

            // 为每个节点检查它是否可以作为串行路径的起点
            for (T node : originalGraph.getAllNodes()) {
                if (!processedNodes.contains(node)) {
                    List<T> path = new ArrayList<>();
                    T current = node;

                    // 判断当前节点是否满足作为串行路径一部分的条件
                    while (current != null && !processedNodes.contains(current)) {
                        path.add(current);
                        processedNodes.add(current);

                        // 检查节点是否有唯一的后继，且该后继有唯一的前驱
                        Set<T> successors = originalGraph.getSuccessors(current);

                        // 如果有且仅有一个后继节点
                        if (successors.size() == 1) {
                            T successor = successors.iterator().next();
                            // 检查后继节点是否只有当前节点作为前驱
                            // 对于节点6，它有一个前驱（节点2）和两个后继（节点7和8）
                            // 我们允许节点6作为串行路径的一部分，因为它有唯一的前驱
                            if (originalGraph.getPredecessors(successor).size() == 1) {
                                current = successor;
                            } else {
                                current = null;
                            }
                        } else {
                            current = null;
                        }
                    }

                    // 如果路径长度大于1，就认为找到了一个串行路径
                    if (path.size() > 1) {
                        serialPaths.add(path);
                    }
                }
            }

            // 重要修复：检查带有多个输出的节点
            // 专门处理像2->6这样的情况，其中6有多个输出，但2和6仍然可以合并
            for (T node : originalGraph.getAllNodes()) {
                if (processedNodes.contains(node)) continue;

                Set<T> successors = originalGraph.getSuccessors(node);
                if (successors.size() == 1) {
                    T successor = successors.iterator().next();
                    // 如果后继节点的前驱只有当前节点，但后继节点有多个输出
                    if (originalGraph.getPredecessors(successor).size() == 1 &&
                            originalGraph.getOutDegree(successor) > 1) {
                        List<T> path = new ArrayList<>();
                        path.add(node);
                        path.add(successor);
                        processedNodes.add(node);
                        processedNodes.add(successor);
                        serialPaths.add(path);
                    }
                }
            }

            return serialPaths;
        }

        /**
         * 构建优化后的图
         */
        private void buildOptimizedGraph(Graph<T> optimizedGraph, List<List<T>> serialPaths) {
            // 为每个串行路径创建合并节点
            createMergedNodes(serialPaths);

            // 添加所有节点到优化后的图
            for (T node : originalGraph.getAllNodes()) {
                // 如果节点是串行路径的一部分，跳过
                if (isPartOfAnySerialPath(node, serialPaths)) {
                    continue;
                }
                optimizedGraph.addNode(node);
            }

            // 添加所有合并节点到优化后的图
            for (T mergedNode : mergedNodeToPathMap.keySet()) {
                optimizedGraph.addNode(mergedNode);
            }

            // 重新连接边
            reconnectEdges(optimizedGraph, serialPaths);
        }

        /**
         * 创建合并节点
         */
        @SuppressWarnings("unchecked")
        private void createMergedNodes(List<List<T>> serialPaths) {
            for (List<T> path : serialPaths) {
                if (path.isEmpty()) continue;

                // 创建一个表示合并后的节点
                T mergedNode = (T) createMergedNodeRepresentation(path);

                // 记录路径和合并节点的映射关系
                pathToMergedNodeMap.put(path, mergedNode);
                mergedNodeToPathMap.put(mergedNode, path);
            }
        }

        /**
         * 创建合并节点的表示
         * 注意：实际实现需要根据T的具体类型来适配
         */
        private List<T> createMergedNodeRepresentation(List<T> path) {
            // 这里只是一个示例，实际需要根据T的具体类型来实现
            List<T> mergedNode = new ArrayList<>();
            for (T node : path) {
                mergedNode.add(node);
            }
            return mergedNode; // 移除最后的连字符
        }

        /**
         * 判断节点是否是任何串行路径的一部分
         */
        private boolean isPartOfAnySerialPath(T node, List<List<T>> serialPaths) {
            for (List<T> path : serialPaths) {
                if (path.contains(node)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 重新连接边
         */
        private void reconnectEdges(Graph<T> optimizedGraph, List<List<T>> serialPaths) {
            // 处理非串行路径节点之间的边
            for (T source : originalGraph.getAllNodes()) {
                if (isPartOfAnySerialPath(source, serialPaths)) {
                    continue;
                }

                for (Edge<T> edge : originalGraph.getOutgoingEdges(source)) {
                    T target = edge.getTarget();

                    // 如果目标节点不是任何路径的一部分，直接添加边
                    if (!isPartOfAnySerialPath(target, serialPaths)) {
                        optimizedGraph.addEdge(source, target, edge.getData());
                    } else {
                        // 如果目标节点是某个路径的一部分，找到对应的合并节点
                        for (List<T> path : serialPaths) {
                            if (path.contains(target) && path.get(0).equals(target)) {
                                T mergedNode = pathToMergedNodeMap.get(path);
                                optimizedGraph.addEdge(source, mergedNode, edge.getData());
                                break;
                            }
                        }
                    }
                }
            }

            // 处理串行路径与其他节点之间的边
            for (List<T> path : serialPaths) {
                if (path.isEmpty()) continue;

                T lastNodeInPath = path.get(path.size() - 1);
                T mergedNode = pathToMergedNodeMap.get(path);

                // 添加从路径最后一个节点到其他节点的边
                for (Edge<T> edge : originalGraph.getOutgoingEdges(lastNodeInPath)) {
                    T target = edge.getTarget();

                    // 如果目标节点不是当前路径的一部分
                    if (!path.contains(target)) {
                        // 如果目标节点是其他路径的一部分，连接到对应的合并节点
                        if (isPartOfAnySerialPath(target, serialPaths)) {
                            for (List<T> targetPath : serialPaths) {
                                if (targetPath.contains(target) && targetPath.get(0).equals(target)) {
                                    T targetMergedNode = pathToMergedNodeMap.get(targetPath);
                                    optimizedGraph.addEdge(mergedNode, targetMergedNode, edge.getData());
                                    break;
                                }
                            }
                        } else {
                            // 如果目标节点不是任何路径的一部分，直接连接
                            optimizedGraph.addEdge(mergedNode, target, edge.getData());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph<String> graph = new Graph<>();
        graph.addEdge("1", "2");
        graph.addEdge("2", "3");
        graph.addEdge("3", "4");
        graph.addEdge("4", "5");
        graph.addEdge("5", "6");
        graph.addEdge("6", "7");
        graph.addEdge("7", "8");
        graph.addEdge("8", "9");
        graph.addEdge("9", "10");

        graph.printGraph();


        // 优化DAG
        Optimizer<String> optimizer = new Optimizer<>(graph);
        Graph<String> optimizedGraph = optimizer.optimize();

        optimizedGraph.printGraph();

    }

//    public static void main(String[] args) {
//        Graph<String> graph = new Graph<>();
//        graph.addEdge("start", "hot_recall");
//        graph.addEdge("start", "user_cf_recall");
//        graph.addEdge("start", "i2i_recall");
//        graph.addEdge("hot_recall", "merge_recall");
//        graph.addEdge("user_cf_recall", "merge_recall");
//        graph.addEdge("i2i_recall", "merge_recall");
//        graph.addEdge("merge_recall", "ctr_rank");
//        graph.addEdge("ctr_rank", "top_rerank");
//        graph.addEdge("top_rerank", "end");
//
//        graph.printGraph();
//
//
//        // 优化DAG
//        Optimizer<String> optimizer = new Optimizer<>(graph);
//        Graph<String> optimizedGraph = optimizer.optimize();
//
//        optimizedGraph.printGraph();
//
//    }




//    public static void main(String[] args) {
////        Graph<String> graph = new Graph<>();
////        graph.addEdge("1", "2");
////        graph.addEdge("1", "3");
////        graph.addEdge("3", "4");
////        graph.addEdge("4", "5");
////        graph.addEdge("5", "9");
////        graph.addEdge("2", "6");
////        graph.addEdge("6", "7");
////        graph.addEdge("7", "9");
////        graph.addEdge("6", "8");
////        graph.addEdge("8", "9");
//
//
//        Graph<String> graph = new Graph<>();
//        graph.addEdge("start", "hot_recall");
//        graph.addEdge("start", "user_cf_recall");
//        graph.addEdge("start", "i2i_recall");
//        graph.addEdge("hot_recall", "merge_recall");
//        graph.addEdge("user_cf_recall", "merge_recall");
//        graph.addEdge("i2i_recall", "merge_recall");
//        graph.addEdge("merge_recall", "ctr_rank");
//        graph.addEdge("ctr_rank", "top_rerank");
//        graph.addEdge("top_rerank", "end");
//
//        graph.printGraph();
//
//
//        // 优化DAG
////        Optimizer<String> optimizer = new Optimizer<>(graph);
////        Graph<String> optimizedGraph = optimizer.optimize();
////
////        optimizedGraph.printGraph();
//
//        DAGToTreeConverter<String> converter = new DAGToTreeConverter<>(graph);
//        TreeNode<String> treeNode = converter.convertToTree();
//        System.out.println(treeNode.getData());
//        for (TreeNode<String> child : treeNode.getChildren()) {
//            System.out.println(child.getData());
//        }
//        System.out.println(1);
//
//    }



//    public static void main(String[] args) {
//        Graph<Node<String>> graph = new Graph<>();
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
//
//        graph.printGraph();
//
//        // 拓扑排序
//        List<Node<String>> result = graph.topologicalSort();
//        System.out.println("拓扑排序结果："+Arrays.toString(result.toArray()));
//
//
//        Optimizer<Node<String>> optimizer = new Optimizer<>(graph);
//        Graph<Node<String>> optimizedGraph = optimizer.optimize();
//
//        optimizedGraph.printGraph();
//
//    }
}
