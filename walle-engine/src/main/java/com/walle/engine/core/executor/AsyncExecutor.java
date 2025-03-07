package com.walle.engine.core.executor;

import com.walle.engine.common.exception.OperatorExecutionException;
import com.walle.engine.core.AsyncEngine;
import com.walle.engine.threadpool.Parallel;
import com.walle.engine.threadpool.ThreadPool;
import com.walle.operator.FlowCtx;
import com.walle.operator.common.constants.Constants;
import com.walle.operator.node.Node;
import com.walle.operator.utils.DAG;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * async executor
 * @author harley.shi
 * @date 2024/7/3
 */
@Slf4j
public class AsyncExecutor<C extends FlowCtx> implements GraphExecutor<C> {
    /*
     * flow dag
     */
    private final AsyncEngine<C> dagEngine;

    /*
     * 任务阻塞队列
     */
    private final BlockingQueue<Node> workQueue;

    /*
     * 节点id入度表映射
     */
    private final Map<Node, AtomicInteger> inDegrees;

    public AsyncExecutor(AsyncEngine<C> dagEngine) {
        this.dagEngine = dagEngine;
        this.workQueue = new LinkedBlockingQueue<>();
        this.inDegrees = dagEngine.copyInDegrees();
    }

    @Override
    public void execute(C context) {
        // 1. 获取开始节点的后继节点
        Set<DAG.Edge<Node>> rootNeighbors = dagEngine.getOutgoingEdges(Constants.START_NODE_DEF);
        // 提交根节点的任务
        rootNeighbors.forEach(edge -> submitWork(edge.getTarget()));
        // 2. 主线程进入事件循环
        eventLoop(context);
    }

    /**
     * 事件循环（主线程逻辑）
     */
    private void eventLoop(C context) {
        Node node;
        while (true){
            try {
                node = workQueue.poll(100L, TimeUnit.MILLISECONDS);
                if(null == node || node.equals(Constants.END_NODE_DEF)){
                    break;
                }
                executeWorkAsync(node, context);
            } catch (InterruptedException e) {
                log.error("{} event loop interrupted.", dagEngine.name(), e);
                break;
            }
        }
    }

    /**
     * 提交任务到任务队列
     */
    private void submitWork(Node node) {
        workQueue.add(node);
    }

    /**
     * 异步执行任务，并设置超时时间
     */
    private <O> void executeWorkAsync(Node node, C ctx) {
        CompletableFuture.supplyAsync(() -> {
                    try{
                        return node.getComponent().execute(ctx);
                    }catch (Exception e){
                        log.error("executeWorkAsync component exec error ", e);
                        throw e;
                    }
                }, Parallel.getExecutor(ThreadPool.Names.KERNEL))
                .thenAccept(result -> handlePostExecution(node))
                .exceptionally(ex -> {
                    log.error("Exception in component nodeId:{} ", node.getNodeId(), ex);
                    throw new OperatorExecutionException(ex);
                });
    }

    private void handlePostExecution(Node node) {
       try{
           for (DAG.Edge<Node> edge : dagEngine.getOutgoingEdges(node)) {
               Node neighbor = edge.getTarget();
               if (inDegrees.get(neighbor).decrementAndGet() == 0) {
                   submitWork(neighbor);
               }
           }
       }catch (Exception e){
           log.error("handlePostExecution error ", e);
           throw new OperatorExecutionException(e);
       }
    }

    @Override
    public String getName() {
        return dagEngine.name();
    }
}
