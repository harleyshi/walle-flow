package com.flow.engine.threadpool;

/**
 * 常量
 */
public interface Constants {

    /**
     * 线程池名称
     */
    String POOL_NAME = "name";

    /**
     * 线程池大小
     */
    String POOL_SIZE = "size";

    /**
     * 线程池队列大小
     */
    String QUEUE_SIZE = "queue_size";

    /**
     * 线程池类型
     */
    String THREAD_POOL_TYPE = "type";

    /**
     * 动态线程池的核心线程数
     */
    String CORE_POOL_SIZE = "core";

    /**
     * 动态线程池最大线程数
     */
    String MAX_POOL_SIZE = "max";

    /**
     * 动态线程池空闲线程存活时间
     */
    String KEEP_ALIVE = "keep_alive";

    /**
     * 线程池满时的拒绝策略
     */
    String REJECT_POLICY = "reject_policy";

    /**
     * 当线程池满时抛弃新增任务，不会抛出异常
     */
    String ABANDON = "abandon";

    /**
     * 当线程池满时拒绝执行新任务并抛出异常
     */
    String REJECT = "reject";
}
