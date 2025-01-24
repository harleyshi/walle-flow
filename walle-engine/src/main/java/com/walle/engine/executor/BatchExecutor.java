//package com.walle.engine.executor;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.walle.engine.threadpool.Parallel;
//import com.walle.engine.threadpool.ThreadPool;
//import com.walle.operator.FlowCtx;
//import com.walle.operator.utils.DirectedGraph;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * bfs executor
// * @author harley.shi
// * @date 2024/7/3
// */
//public class BatchExecutor<C extends FlowCtx> extends GraphExecutor<C> {
//    /*
//     * flow dag
//     */
//    private final DAGEngine<C> dagEngine;
//
//    /*
//     * 任务阻塞队列
//     */
//    private final BlockingQueue<String> workQueue;
//
//    /*
//     * 节点id入度表映射
//     */
//    private final Map<String, AtomicInteger> inDegrees;
//
//    /*
//     * 未完成任务计数器
//     */
//    private final AtomicInteger pendingTasks;
//
//    public BatchExecutor(DAGEngine<C> dagEngine) {
//        this.dagEngine = dagEngine;
//        this.workQueue = new LinkedBlockingQueue<>();
//        this.inDegrees = dagEngine.copyInDegrees();
//        this.pendingTasks = new AtomicInteger(0);
//    }
//
//    @Override
//    void doExecute(C context) {
//        // 1. 找出所有入度为0的节点（根节点）
//        List<String> rootNodes = dagEngine.getNodesWithZeroInDegree();
//        // 提交根节点的任务
//        rootNodes.forEach(this::submitWork);
//        // 2. 主线程进入事件循环
//        eventLoop(context);
//    }
//
//    /**
//     * 事件循环（主线程逻辑）
//     */
//    private void eventLoop(C context) {
//        while (pendingTasks.get() > 0) {
//            try {
//                // 从任务队列中获取任务，等待最多 100 毫秒
//                String nodeId = workQueue.take();
//                if(nodeId.equals("-999999999999")){
//                    break;
//                }
//                // 提交任务给Worker线程池执行
//                executeWorkAsync(nodeId, context);
//            } catch (InterruptedException e) {
//                System.err.println("Event loop interrupted.");
//                break;
//            }
//        }
//    }
//
//    /**
//     * 提交任务到任务队列
//     */
//    private void submitWork(String nodeId) {
//        // 将任务加入队列
//        workQueue.add(nodeId);
//        // 增加未完成任务计数
//        pendingTasks.incrementAndGet();
//    }
//
//    /**
//     * 异步执行任务，并设置超时时间
//     */
//    private void executeWorkAsync(String nodeId, C ctx) {
//        if(StringUtils.isBlank(nodeId)){
//            pendingTasks.decrementAndGet();
//            return;
//        }
//        IComponent<?, C> component = dagEngine.getComponent(nodeId);
//        CompletableFuture.supplyAsync(() -> {
//                    return component.execute(ctx);
//                }, Parallel.getExecutor(ThreadPool.Names.KERNEL))
//                // 设置超时时间为 3 秒
////                .orTimeout(3000, TimeUnit.MILLISECONDS)
//                .thenAccept(result -> {
//                    Set<DirectedGraph.Edge<String>> nodeNeighbors = dagEngine.getNeighbors(nodeId);
//                    // 最后一个节点
//                    if(CollectionUtil.isEmpty(nodeNeighbors)){
//                        pendingTasks.decrementAndGet();
//                        workQueue.add("-999999999999");
//                        return;
//                    }
//                    // 任务完成后的回调逻辑
//                    for (DirectedGraph.Edge<String> edge : nodeNeighbors) {
//                        String neighbor = edge.getTarget();
//                        AtomicInteger inDegree = inDegrees.get(neighbor);
//
//                        if (inDegree.decrementAndGet() == 0) {
//                            submitWork(neighbor); // 提交下级任务
////                            // 先判断nodeCode节点是否为条件节点，如果是的话再判断条件是否满足，如果满足则提交下级任务
////                            if(result.equals(edge.getData())){
////                                submitWork(neighbor); // 提交下级任务
////                            }
//                        }
//                    }
//                    pendingTasks.decrementAndGet(); // 减少未完成任务计数
//                })
//                .exceptionally(ex -> {
//                    pendingTasks.decrementAndGet(); // 减少未完成任务计数
//                    return null;
//                });
//    }
//
//    @Override
//    public String getName() {
//        return dagEngine.getName();
//    }
//}
