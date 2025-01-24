package com.walle.engine.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Parallel {

    /**
     * 并行执行分块数量，超过该数量进行分块并行执行
     */
    public static final int BLOCK_SIZE = 250;

    private static final ThreadPool threadPool;

    static {
        Settings kernel = Settings.builder()
                .put(Constants.THREAD_POOL_TYPE, "scaling")
                .put(Constants.KEEP_ALIVE, "60s")
                .put(Constants.POOL_NAME, ThreadPool.Names.KERNEL)
                .put(Constants.CORE_POOL_SIZE, 200)
                .put(Constants.MAX_POOL_SIZE, 800)
                .build();
        Settings generic = Settings.builder()
                .put(Constants.THREAD_POOL_TYPE, "fixed")
                .put(Constants.POOL_NAME, ThreadPool.Names.MANAGEMENT)
                .put(Constants.POOL_SIZE, 10)
                .put(Constants.QUEUE_SIZE, 200)
                .build();
        Settings others = Settings.builder()
                .put(Constants.THREAD_POOL_TYPE, "fixed")
                .put(Constants.POOL_NAME, ThreadPool.Names.OTHERS)
                .put(Constants.POOL_SIZE, 50)
                .put(Constants.QUEUE_SIZE, 400)
                .build();
        threadPool = new ThreadPool(Arrays.asList(kernel, generic, others));
    }

    public static void awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
        threadPool.awaitTermination(duration, unit);
    }

    /**
     * 获取一个线程池执行器
     */
    public static ExecutorService getExecutor(String name) {
        return threadPool.executor(name);
    }

    /**
     * 提交任务到线程池执行
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return threadPool.executor(ThreadPool.Names.KERNEL).submit(task);
    }

    /**
     * 线程池当前状态
     */
    public static ThreadPoolStats stats() {
        return threadPool.stats();
    }

}
