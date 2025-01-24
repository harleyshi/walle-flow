package com.flow.engine.threadpool;

import com.flow.engine.exception.XRejectedExecutionException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * executor构造器.
 */
public class ExecutorBuilder {

    /**
     * 构建线程池
     * @param settings
     * @param threadContext
     * @return
     */
    static ThreadPool.ExecutorHolder build(final Settings settings, final ThreadContext threadContext) {
        String type = settings.getString(Constants.THREAD_POOL_TYPE);
        ThreadPoolType poolType = ThreadPoolType.fromType(type);

        ThreadPool.ExecutorHolder holder = null;
        // fixed pool
        if (ThreadPoolType.FIXED == poolType) {
            // 固定大小线程池
            int size = Integer.parseInt(settings.getString(Constants.POOL_SIZE));
            int queueSize = Integer.parseInt(settings.getString(Constants.QUEUE_SIZE));
            String name = settings.getString(Constants.POOL_NAME);

            final ThreadFactory threadFactory = NamedThreadFactory.newThreadFactory(name);
            // 可记录队列占用大小并可无锁返回size
            final BlockingQueue<Runnable> queue = new SizeBlockingQueue<>(new LinkedTransferQueue<>(), queueSize);
            // 线程池满时的拒绝策略
            final XRejectedExecutionHandler rejectHandler = rejectedExecutionHandler(settings.getString(Constants.REJECT_POLICY));
            // 封装的executor
            final ThreadPoolExecutor executor = new NamedThreadPoolExecutor(
                    name, size, size, 0, TimeUnit.MILLISECONDS,
                    queue, threadFactory, rejectHandler, threadContext
            );
            // 用于线程监控
            final ThreadPool.Info info = new ThreadPool.Info(name, ThreadPoolType.FIXED, size, size, null, queueSize);
            holder = new ThreadPool.ExecutorHolder(executor, info);
        }
        // 可伸缩的线程池
        else if (ThreadPoolType.SCALING == poolType) {
            // scaling pool
            TimeValue keepAlive = TimeValue.parseTimeValue(settings.getString(Constants.KEEP_ALIVE), Constants.KEEP_ALIVE);
            int core = Integer.parseInt(settings.getString(Constants.CORE_POOL_SIZE));
            int max = Integer.parseInt(settings.getString(Constants.MAX_POOL_SIZE));
            String name = settings.getString(Constants.POOL_NAME);

            final ThreadPool.Info info = new ThreadPool.Info(name, ThreadPoolType.SCALING, core, max, keepAlive, -1);

            final ThreadFactory threadFactory = NamedThreadFactory.newThreadFactory(name);
            // 无界队列，强制重试策略
            ExecutorScalingQueue<Runnable> queue = new ExecutorScalingQueue<>();
            final ThreadPoolExecutor executor = new NamedThreadPoolExecutor(
                    name, core, max, keepAlive.millis(), TimeUnit.MILLISECONDS,
                    queue, threadFactory,
                    new ForceQueuePolicy(), threadContext
            );
            queue.executor = executor;
            holder = new ThreadPool.ExecutorHolder(executor, info);
        }

        return holder;
    }

    static XRejectedExecutionHandler rejectedExecutionHandler(String name) {
        if (Constants.ABANDON.equals(name)) {
            return new XRejectedExecutionHandler() {
                private LongAdder counter = new LongAdder();

                @Override
                public long rejected() {
                    return counter.sum();
                }

                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    counter.increment();
                }
            };
        } else {
            return new CountAbortPolicy();
        }
    }

    /**
     * 改造后的队列，当线程池大小尚未到最大，先new线程，线程池大小到最大后才放队列
     * @param <E>
     */
    static class ExecutorScalingQueue<E> extends LinkedTransferQueue<E> {

        ThreadPoolExecutor executor;

        public ExecutorScalingQueue() {
        }

        @Override
        public boolean offer(E e) {
            // first try to transfer to a waiting worker thread
            if (!tryTransfer(e)) {
                // check if there might be spare capacity in the thread
                // pool executor
                int left = executor.getMaximumPoolSize() - executor.getCorePoolSize();
                if (left > 0) {
                    // reject queuing the task to force the thread pool
                    // executor to add a worker if it can; combined
                    // with ForceQueuePolicy, this causes the thread
                    // pool to always scale up to max pool size and we
                    // only queue when there is no spare capacity
                    return false;
                } else {
                    return super.offer(e);
                }
            } else {
                return true;
            }
        }

    }
    /**
     * A handler for rejected tasks that adds the specified element to this queue,
     * waiting if necessary for space to become available.
     */
    static class ForceQueuePolicy implements XRejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                //should never happen since we never wait
                throw new XRejectedExecutionException(e);
            }
        }

        @Override
        public long rejected() {
            return 0;
        }
    }

    /**
     * 可以记录被拒绝的请求数
     */
    static class CountAbortPolicy implements XRejectedExecutionHandler {
        private final LongAdder rejected = new LongAdder();

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof SafeRunnable) {
                if (((SafeRunnable) r).isForceExecution()) {
                    BlockingQueue<Runnable> queue = executor.getQueue();
                    if (!(queue instanceof SizeBlockingQueue)) {
                        throw new IllegalStateException("forced execution, but expected a size queue");
                    }
                    try {
                        ((SizeBlockingQueue) queue).forcePut(r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("forced execution, but got interrupted", e);
                    }
                    return;
                }
            }
            rejected.increment();
            throw new XRejectedExecutionException("rejected execution of " + r + " on " + executor, executor.isShutdown());
        }

        @Override
        public long rejected() {
            return rejected.sum();
        }
    }

}
