package com.flow.engine.test;

import com.flow.engine.threadpool.Parallel;
import com.flow.engine.threadpool.ThreadPool;
import com.flow.engine.utils.DirectedGraph;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ReactiveExecutor_a<T> {

    private final DirectedGraph<T> graph; // DAG
    private final BlockingQueue<T> taskQueue; // 任务阻塞队列
    private final Executor executor; // 线程池
    private final Map<T, AtomicInteger> inDegrees; // 入度表（使用 AtomicInteger）

    public ReactiveExecutor_a(DirectedGraph<T> graph) {
        this.graph = graph;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executor = Parallel.getExecutor(ThreadPool.Names.KERNEL);
        this.inDegrees = new ConcurrentHashMap<>();
        for (T node : graph.getAllNodes()) {
            inDegrees.put(node, new AtomicInteger(graph.getInDegree(node))); // 初始化入度
        }
    }

    /**
     * 启动执行器
     */
    public void start() {
        // 1. 找出所有入度为0的节点（根节点）
        List<T> rootNodes = graph.getNodesWithZeroInDegree();
        for (T node : rootNodes) {
            submitTask(node); // 将根节点提交到任务队列
        }

        // 2. 主线程进入事件循环
        eventLoop();

        // 3. 所有任务完成后，输出完成信息
        System.out.println("All tasks completed.");
    }

    /**
     * 事件循环（主线程逻辑）
     */
    private void eventLoop() {
        while (true) {
            try {
                // 从任务队列中获取任务，等待最多 100 毫秒
                T node = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                if (node != null) {
                    // 提交任务给线程池执行
                    executor.execute(() -> executeTask(node));
                } else {
                    // 如果队列为空且没有新的任务被提交，退出事件循环
                    if (taskQueue.isEmpty() && noPendingTasks()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Event loop interrupted.");
                break;
            }
        }
    }

    /**
     * 检查是否没有未完成的任务
     */
    private boolean noPendingTasks() {
        for (AtomicInteger inDegree : inDegrees.values()) {
            if (inDegree.get() > 0) {
                return false; // 仍有未完成的任务
            }
        }
        return true; // 所有任务已完成
    }

    /**
     * 提交任务到任务队列
     */
    private void submitTask(T node) {
        taskQueue.add(node); // 将任务加入队列
    }

    /**
     * 执行任务（子线程逻辑）
     */
    private void executeTask(T node) {
        try {
            // 1. 执行任务
            Object result = simulateTaskExecution(node);
            System.out.println("Node " + node + " executed with result: " + result);

            // 2. 找出当前节点的所有可执行下级节点
            for (DirectedGraph.Edge<T> edge : graph.getNeighbors(node)) {
                T neighbor = edge.getTarget();
                AtomicInteger inDegree = inDegrees.get(neighbor);
                if (inDegree.decrementAndGet() == 0) {
                    // 3. 将入度为0的节点提交到任务队列
                    submitTask(neighbor);
                }
            }
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

        ReactiveExecutor_a<String> executor = new ReactiveExecutor_a<>(graph);
        executor.start();
    }
}