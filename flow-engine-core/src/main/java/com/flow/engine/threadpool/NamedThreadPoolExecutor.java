package com.flow.engine.threadpool;

import com.flow.engine.exception.XRejectedExecutionException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 定制executor，用来记录reject等异常处理，也加些监控采集.
 * <p>
 */
public class NamedThreadPoolExecutor extends ThreadPoolExecutor {

    private final ThreadContext contextHolder;
    private volatile ShutdownListener listener;

    private final Object monitor = new Object();
    /**
     * Name used in error reporting.
     */
    private final String name;

    NamedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, ThreadContext contextHolder) {
        this(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new ExecutorBuilder.CountAbortPolicy(), contextHolder);
    }

    NamedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, XRejectedExecutionHandler handler,
                            ThreadContext contextHolder) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.name = name;
        this.contextHolder = contextHolder;
    }

    /**
     * 关闭线程池并回调设置的监听器方法
     * @param listener
     */
    public void shutdown(ShutdownListener listener) {
        synchronized (monitor) {
            if (this.listener != null) {
                throw new IllegalStateException("Shutdown was already called on this thread pool");
            }
            if (isTerminated()) {
                listener.onTerminated();
            } else {
                this.listener = listener;
            }
        }
        shutdown();
    }

    @Override
    protected synchronized void terminated() {
        super.terminated();
        synchronized (monitor) {
            if (listener != null) {
                try {
                    listener.onTerminated();
                } finally {
                    listener = null;
                }
            }
        }
    }

    /**
     * 线程池关闭时触发
     */
    public interface ShutdownListener {
        void onTerminated();
    }

    @Override
    public void execute(final Runnable command) {
        // 这里提交任务都会包装一次，用来传递线程上下文
        doExecute(wrapRunnable(command));
    }

    protected void doExecute(final Runnable command) {
        try {
            super.execute(command);
        } catch (XRejectedExecutionException ex) {
            if (command instanceof SafeRunnable) {
                // If we are an abstract runnable we can handle the rejection
                // directly and don't need to rethrow it.
                try {
                    ((SafeRunnable) command).onRejection(ex);
                } finally {
                    ((SafeRunnable) command).onAfter();
                }
            } else {
                throw ex;
            }
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    /**
     * Returns a stream of all pending tasks. This is similar to {@link #getQueue()} but will expose the originally submitted
     * {@link Runnable} instances rather than potentially wrapped ones.
     */
    public Stream<Runnable> getTasks() {
        return this.getQueue().stream().map(this::unwrap);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append('[');
        b.append(name).append(", ");
        if (getQueue() instanceof SizeBlockingQueue) {
            @SuppressWarnings("rawtypes")
            SizeBlockingQueue queue = (SizeBlockingQueue) getQueue();
            b.append("queue capacity = ").append(queue.capacity()).append(", ");
        }
        /*
         * ThreadPoolExecutor has some nice information in its toString but we
         * can't get at it easily without just getting the toString.
         */
        b.append(super.toString()).append(']');
        return b.toString();
    }

    /**
     * 包装
     */
    protected Runnable wrapRunnable(Runnable command) {
        return contextHolder.preserveContext(command);
    }

    /**
     * 拆包
     */
    protected Runnable unwrap(Runnable runnable) {
        return contextHolder.unwrap(runnable);
    }

}
