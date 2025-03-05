//package com.walle.engine.executor;
//
//import com.walle.engine.common.exception.OperatorExecutionException;
//import com.walle.engine.threadpool.Parallel;
//import com.walle.engine.threadpool.ThreadPool;
//import com.walle.operator.FlowCtx;
//import com.walle.operator.utils.DAG;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * async executor
// * @author harley.shi
// * @date 2024/7/3
// */
//public class AsyncExecutor_v1<C extends FlowCtx> implements GraphExecutor<C> {
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
//    public AsyncExecutor_v1(DAGEngine<C> dagEngine) {
//        this.dagEngine = dagEngine;
//        this.workQueue = new LinkedBlockingQueue<>();
//        this.inDegrees = dagEngine.copyInDegrees();
//    }
//
//    @Override
//    public void execute(C context) {
//        // 1. 找出所有入度为0的节点（根节点）
//        Set<DAG.Edge<String>> rootNeighbors = dagEngine.getOutgoingEdges("0");
//        // 提交根节点的任务
//        rootNeighbors.forEach(edge -> submitWork(edge.getTarget()));
//        // 2. 主线程进入事件循环
//        eventLoop(context);
//    }
//
//    /**
//     * 事件循环（主线程逻辑）
//     */
//    private void eventLoop(C context) {
//        String nodeId;
//        while (true){
//            try {
//                // 从任务队列中获取任务，等待最多 100 毫秒
//                nodeId = workQueue.take();
//                if(nodeId.equals("99999999")){
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
//    }
//
//    /**
//     * 异步执行任务，并设置超时时间
//     */
//    private void executeWorkAsync(String nodeId, C ctx) {
//        CompletableFuture.supplyAsync(() -> dagEngine.getComponent(nodeId).execute(ctx), Parallel.getExecutor(ThreadPool.Names.KERNEL))
//                .thenAccept(result -> handlePostExecution(nodeId))
//                .exceptionally(ex -> {
//                    System.err.println("Exception in component " + nodeId + ": " + ex.getMessage());
//                    throw new OperatorExecutionException(ex);
//                });
//    }
//
//    private void handlePostExecution(String nodeId) {
//        // 更新后续节点入度并提交任务
//        Set<DAG.Edge<String>> nodeNeighbors = dagEngine.getOutgoingEdges(nodeId);
//        for (DAG.Edge<String> edge : nodeNeighbors) {
//            String neighbor = edge.getTarget();
//            AtomicInteger inDegree = inDegrees.get(neighbor);
//            if (inDegree.decrementAndGet() == 0) {
//                submitWork(neighbor);
//            }
//        }
//    }
//
//    @Override
//    public String getName() {
//        return dagEngine.getName();
//    }
//}
