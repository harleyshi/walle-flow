package com.walle.engine.executor;

import cn.hutool.core.collection.CollectionUtil;
import com.walle.engine.threadpool.Parallel;
import com.walle.engine.threadpool.ThreadPool;
import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;
import com.walle.operator.component.IfComponent;
import com.walle.operator.utils.DirectedGraph;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
public class AsyncExecutor<C extends FlowCtx> implements GraphExecutor<C> {
    /*
     * flow dag
     */
    private final DAGEngine<C> dagEngine;

    /*
     * 任务阻塞队列
     */
    private final BlockingQueue<String> workQueue;

    /*
     * 节点id入度表映射
     */
    private final Map<String, AtomicInteger> inDegrees;

    public AsyncExecutor(DAGEngine<C> dagEngine) {
        this.dagEngine = dagEngine;
        this.workQueue = new LinkedBlockingQueue<>();
        this.inDegrees = dagEngine.copyInDegrees();
    }

    @Override
    public void execute(C context) {
        // 1. 找出所有入度为0的节点（根节点）
        Set<DirectedGraph.Edge<String>> rootNeighbors = dagEngine.getNeighbors("0");
        // 提交根节点的任务
        rootNeighbors.forEach(edge -> submitWork(edge.getTarget()));
        // 2. 主线程进入事件循环
        eventLoop(context);
    }

    /**
     * 事件循环（主线程逻辑）
     */
    private void eventLoop(C context) {
        String nodeId;
        while (true){
            try {
                // 从任务队列中获取任务，等待最多 100 毫秒
                nodeId = workQueue.take();
                if(nodeId.equals("99999999")){
                    break;
                }
                // 提交任务给Worker线程池执行
                executeWorkAsync(nodeId, context);
            } catch (InterruptedException e) {
                System.err.println("Event loop interrupted.");
                break;
            }
        }
    }

    /**
     * 提交任务到任务队列
     */
    private void submitWork(String nodeId) {
        // 将任务加入队列
        workQueue.add(nodeId);
    }

    /**
     * 异步执行任务，并设置超时时间
     */
    @SuppressWarnings("unchecked")
    private <O> void executeWorkAsync(String nodeId, C ctx) {
        IComponent<C,O> component = (IComponent<C, O>) dagEngine.getComponent(nodeId);
        CompletableFuture.supplyAsync(() -> {
                    // 获取上一个节点的结果作为参数
                    return component.execute(ctx);
                }, Parallel.getExecutor(ThreadPool.Names.KERNEL))
                .thenAccept(result -> {
                    // 当节点执行完后将其出度的节点入度减一
                    Set<DirectedGraph.Edge<String>> nodeNeighbors = dagEngine.getNeighbors(nodeId);
                    if(component instanceof IfComponent){
                        // IfComponent 的特殊处理
                        for (DirectedGraph.Edge<String> edge : nodeNeighbors) {
                            String neighbor = edge.getTarget();
                            AtomicInteger inDegree = inDegrees.get(neighbor);
                            if (result != null && String.valueOf(result).equals(edge.getData())) {
                                // 满足条件的分支，减少入度并提交任务
                                if (inDegree.decrementAndGet() == 0) {
                                    submitWork(neighbor);
                                }
                            } else {
                                // 不满足条件的分支，直接减少入度，但不提交任务
                                inDegree.decrementAndGet();

                                Set<DirectedGraph.Edge<String>> nodeNeighbors1 = dagEngine.getNeighbors(neighbor);
                                for (DirectedGraph.Edge<String> edge1 : nodeNeighbors1) {
                                    String neighbor1 = edge1.getTarget();
                                    AtomicInteger inDegree1 = inDegrees.get(neighbor1);
                                    inDegree1.decrementAndGet();
                                }
                            }
                        }
                    }else{
                        for (DirectedGraph.Edge<String> edge : nodeNeighbors) {
                            String neighbor = edge.getTarget();
                            AtomicInteger inDegree = inDegrees.get(neighbor);
                            if (inDegree.decrementAndGet() == 0) {
                                submitWork(neighbor); // 提交下级任务
                            }
                        }
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Exception in component " + nodeId + ": " + ex.getMessage());
                    return null;
                });
    }

    @Override
    public String getName() {
        return dagEngine.getName();
    }
}
