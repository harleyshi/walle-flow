package com.walle.engine.executor;

import com.walle.engine.common.exception.OperatorExecutionException;
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
    private final DAGEngine<C> dagEngine;

    /*
     * 任务阻塞队列
     */
    private final BlockingQueue<Node> workQueue;

    /*
     * 节点id入度表映射
     */
    private final Map<Node, AtomicInteger> inDegrees;

    public AsyncExecutor(DAGEngine<C> dagEngine) {
        this.dagEngine = dagEngine;
        this.workQueue = new LinkedBlockingQueue<>();
        this.inDegrees = dagEngine.getDagGraph().copyInDegrees();
    }

    @Override
    public void execute(C context) {
        // 1. 找出所有入度为0的节点（根节点）
        Set<DAG.Edge> rootNeighbors = dagEngine.getOutgoingEdges(Constants.START_NODE);
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
                // 从任务队列中获取任务，等待最多 100 毫秒
                node = workQueue.take();
                if(node.equals(Constants.END_NODE)){
                    break;
                }
                // 提交任务给Worker线程池执行
                executeWorkAsync(node, context);
            } catch (InterruptedException e) {
                System.err.println("Event loop interrupted.");
                break;
            }
        }
    }

    /**
     * 提交任务到任务队列
     */
    private void submitWork(Node node) {
        // 将任务加入队列
        workQueue.add(node);
    }

    /**
     * 异步执行任务，并设置超时时间
     */
    private <O> void executeWorkAsync(Node node, C ctx) {
        CompletableFuture.supplyAsync(() -> {
                    try{
                        if(node.isSingle()){
                            return dagEngine.getComponent(node.getNodeId()).execute(ctx);
                        }
                        for (String nodeId : node.getNodeIds()) {
                            dagEngine.getComponent(nodeId).execute(ctx);
                        }
                        return null;
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
           // 更新后续节点入度并提交任务
           Set<DAG.Edge> nodeNeighbors = dagEngine.getOutgoingEdges(node);
           for (DAG.Edge edge : nodeNeighbors) {
               Node neighbor = edge.getTarget();
               AtomicInteger inDegree = inDegrees.get(neighbor);
               if (inDegree.decrementAndGet() == 0) {
                   submitWork(neighbor);
               }
           }
       }catch (Exception e){
           log.error("handlePostExecution error ", e);
           throw e;
       }
    }

    @Override
    public String getName() {
        return dagEngine.getName();
    }
}
