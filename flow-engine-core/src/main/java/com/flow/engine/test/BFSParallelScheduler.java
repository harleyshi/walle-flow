package com.flow.engine.test;

import com.flow.engine.utils.DirectedGraph;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BFSParallelScheduler<T> {

    private final DirectedGraph<T> graph; // DAG
    private final ExecutorService executor; // 线程池
    private final Map<T, AtomicInteger> inDegrees; // 入度表

    public BFSParallelScheduler(DirectedGraph<T> graph, int poolSize) {
        this.graph = graph;
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.inDegrees = graph.copyInDegrees(); // 复制入度表
    }

    /**
     * 启动调度器
     */
    public void start() {
        // 1. 找出所有入度为0的节点（根节点）
        List<T> rootNodes = graph.getNodesWithZeroInDegree();

        // 2. 使用 BFS 遍历 DAG，按层调度任务
        bfsSchedule(rootNodes);

        // 关闭线程池
        executor.shutdown();
    }

    /**
     * 广度优先遍历并调度任务
     */
    private void bfsSchedule(List<T> currentLevelNodes) {
        if (currentLevelNodes.isEmpty()) {
            return; // 所有节点已处理完毕
        }

        // 1. 提交当前层的所有任务到线程池
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (T node : currentLevelNodes) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> executeNode(node), executor);
            futures.add(future);
        }

        // 2. 等待当前层的所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 3. 找出下一层的节点
        List<T> nextLevelNodes = new ArrayList<>();
        for (T node : currentLevelNodes) {
            for (DirectedGraph.Edge<T> edge : graph.getNeighbors(node)) {
                T neighbor = edge.getTarget();
                synchronized (inDegrees) {
                    int newInDegree = inDegrees.get(neighbor).decrementAndGet();
                    if (newInDegree == 0) {
                        nextLevelNodes.add(neighbor); // 将入度为0的节点加入下一层
                    }
                }
            }
        }

        // 4. 递归调度下一层
        bfsSchedule(nextLevelNodes);
    }

    /**
     * 执行节点任务
     */
    private void executeNode(T node) {
        try {
            // 1. 执行任务，并将结果存入缓存
            Object result = simulateTaskExecution(node);
            System.out.println("Node " + node + " executed with result: " + result);
        } catch (Exception e) {
            System.err.println("Error executing node " + node + ": " + e.getMessage());
        }
    }

    /**
     * 模拟任务执行
     */
    private Object simulateTaskExecution(T node) throws InterruptedException {
        // 模拟任务耗时
        Thread.sleep((long) (Math.random() * 1000));
        return "Result_of_" + node; // 返回模拟结果
    }

    public static void main(String[] args) {
        // 创建DAG
        DirectedGraph<String> graph = new DirectedGraph<>();
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

        // 创建调度器并启动
        BFSParallelScheduler<String> scheduler = new BFSParallelScheduler<>(graph, 4);
        scheduler.start();
    }
}