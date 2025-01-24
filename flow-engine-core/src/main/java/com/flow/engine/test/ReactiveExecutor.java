package com.flow.engine.test;
import com.flow.engine.threadpool.Parallel;
import com.flow.engine.threadpool.ThreadPool;
import com.flow.engine.utils.DirectedGraph;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


import java.util.List;
import java.util.Map;
public class ReactiveExecutor<T> {

    private final DirectedGraph<T> graph; // DAG
    private final BlockingQueue<T> taskQueue; // 任务阻塞队列
    private final Map<T, AtomicInteger> inDegrees; // 入度表（使用 AtomicInteger）
    private final AtomicInteger pendingTasks; // 未完成任务计数器

    public ReactiveExecutor(DirectedGraph<T> graph) {
        this.graph = graph;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.inDegrees = new ConcurrentHashMap<>();
        for (T node : graph.getAllNodes()) {
            inDegrees.put(node, new AtomicInteger(graph.getInDegree(node))); // 初始化入度
        }
        this.pendingTasks = new AtomicInteger(0);
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
        while (pendingTasks.get() > 0) {
            try {
                // 从任务队列中获取任务，等待最多 100 毫秒
                T node = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                if (node != null) {
                    // 提交任务给Worker线程池执行
                    executeTaskAsync(node);
                } else {
                    // 如果等待超时且没有任务，检查是否所有任务已完成
                    if (pendingTasks.get() == 0) {
                        break; // 退出事件循环
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
     * 提交任务到任务队列
     */
    private void submitTask(T node) {
        taskQueue.add(node); // 将任务加入队列
        pendingTasks.incrementAndGet(); // 增加未完成任务计数
    }

    /**
     * 异步执行任务，并设置超时时间
     */
    private void executeTaskAsync(T node) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return simulateTaskExecution(node); // 模拟耗时任务
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, Parallel.getExecutor(ThreadPool.Names.KERNEL))
                .orTimeout(3000, TimeUnit.MILLISECONDS) // 设置超时时间为 3 秒
                .thenAccept(result -> {
                    // 任务完成后的回调逻辑
                    System.out.println("Node " + node + " executed with result: " + result);
                    for (DirectedGraph.Edge<T> edge : graph.getNeighbors(node)) {
                        T neighbor = edge.getTarget();
                        AtomicInteger inDegree = inDegrees.get(neighbor);
                        if (inDegree.decrementAndGet() == 0) {
                            submitTask(neighbor); // 提交下级任务
                        }
                    }
                    pendingTasks.decrementAndGet(); // 减少未完成任务计数
                })
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        System.err.println("Node " + node + " execution timed out.");
                    } else {
                        System.err.println("Error executing node " + node + ": " + ex.getMessage());
                    }
                    pendingTasks.decrementAndGet(); // 减少未完成任务计数
                    return null;
                });
    }

    /**
     * 模拟任务执行
     */
    private Object simulateTaskExecution(T node) throws InterruptedException {
        // 模拟任务耗时
        Thread.sleep((long) (Math.random() * 300)); // 随机耗时 0-5 秒
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

        ReactiveExecutor<String> executor = new ReactiveExecutor<>(graph);
        executor.start();
    }
}