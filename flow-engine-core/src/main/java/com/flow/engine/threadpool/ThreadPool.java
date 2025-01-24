package com.flow.engine.threadpool;


import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 包装后的线程池，参考"org.elasticsearch.threadpool.ThreadPool"，拆分了一部分代码到{@link ExecutorBuilder}
 */
public class ThreadPool implements Closeable {

    private final static Set<String> THREAD_POOLS;

    public static class Names {
        public static final String SAME = "same";
        public static final String GENERIC = "generic";
        public static final String LISTENER = "listener";
        public static final String PIPELINE = "pipeline";
        public static final String MANAGEMENT = "management";
        public static final String KERNEL = "kernel";
        public static final String OTHERS = "others";
    }

    static {
        Set<String> poolTypes = new HashSet<>();
        // 这个变量的声明要放到静态代码块之前，初始化顺序问题
        poolTypes.add(Names.SAME);
        poolTypes.add(Names.GENERIC);
        poolTypes.add(Names.LISTENER);
        poolTypes.add(Names.PIPELINE);
        poolTypes.add(Names.MANAGEMENT);
        poolTypes.add(Names.KERNEL);
        poolTypes.add(Names.OTHERS);
        THREAD_POOLS = Collections.unmodifiableSet(poolTypes);
    }

    private final ThreadContext threadContext;

    private Map<String, ExecutorHolder> executors = new HashMap<>();

    /**
     * 构建线程池，可以考虑把settings合并成一个
     * @param settings
     */
    public ThreadPool(List<Settings> settings) {
        // 初始化线程池，其实可以参考es的实现改造下
        // 暂时只用了空设置，其实可以加些service chain，是否拆分等信息
        threadContext = new ThreadContext(Settings.EMPTY);

        for (Settings setting : settings) {
            String poolName = setting.getString(Constants.POOL_NAME);

            // check if valid
            if (!THREAD_POOLS.contains(poolName)) {
                continue;
            }

            ExecutorHolder holder = ExecutorBuilder.build(setting, threadContext);
            executors.put(poolName, holder);
        }

        this.executors = Collections.unmodifiableMap(executors);
    }

    /**
     * Get the executor service with the given name. This executor service's {@link Executor#execute(Runnable)} method will run the
     * {@link Runnable} it is given in the {@link ThreadContext} of the thread that queues it.
     *
     * @param name the name of the executor service to obtain
     * @throws IllegalArgumentException if no executor service with the specified name exists
     */
    public ExecutorService executor(String name) {
        final ExecutorHolder holder = executors.get(name);
        if (holder == null) {
            throw new IllegalArgumentException("no executor service found for [" + name + "]");
        }
        return holder.executor();
    }

    /**
     * 返回线程上下文
     * @return
     */
    public ThreadContext getThreadContext() {
        return threadContext;
    }

    /**
     * 关闭线程池, see {@link ExecutorService#shutdown()}
     */
    public void shutdown() {
        for (ExecutorHolder executor : executors.values()) {
            if (executor.executor() instanceof ThreadPoolExecutor) {
                executor.executor().shutdown();
            }
        }
    }

    /**
     * 立即关闭线程池, see {@link ExecutorService#shutdownNow()}
     */
    public void shutdownNow() {
        for (ExecutorHolder executor : executors.values()) {
            if (executor.executor() instanceof ThreadPoolExecutor) {
                executor.executor().shutdownNow();
            }
        }
    }

    /**
     * 等待线程池关闭，参看 {@link ExecutorService#awaitTermination(long, TimeUnit)}
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        boolean result = true;
        for (ExecutorHolder executor : executors.values()) {
            if (executor.executor() instanceof ThreadPoolExecutor) {
                result &= executor.executor().awaitTermination(timeout, unit);
            }
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (null != threadContext) {
            threadContext.close();
        }
    }

    public ThreadPoolStats stats() {
        List<ThreadPoolStats.Stats> stats = new ArrayList<>();
        for (ExecutorHolder holder : executors.values()) {
            String name = holder.info.getName();
            // no need to have info on "same" thread pool
            if ("same".equals(name)) {
                continue;
            }
            int threads = -1;
            int queue = -1;
            int active = -1;
            long rejected = -1;
            int largest = -1;
            long completed = -1;
            if (holder.executor() instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) holder.executor();
                threads = threadPoolExecutor.getPoolSize();
                queue = threadPoolExecutor.getQueue().size();
                active = threadPoolExecutor.getActiveCount();
                largest = threadPoolExecutor.getLargestPoolSize();
                completed = threadPoolExecutor.getCompletedTaskCount();
                RejectedExecutionHandler rejectedExecutionHandler = threadPoolExecutor.getRejectedExecutionHandler();
                if (rejectedExecutionHandler instanceof XRejectedExecutionHandler) {
                    rejected = ((XRejectedExecutionHandler) rejectedExecutionHandler).rejected();
                }
            }
            stats.add(new ThreadPoolStats.Stats(name, threads, queue, active, rejected, largest, completed));
        }
        return new ThreadPoolStats(stats);
    }

    static class ExecutorHolder {
        private final ExecutorService executor;
        public final Info info;

        ExecutorHolder(ExecutorService executor, Info info) {
            this.executor = executor;
            this.info = info;
        }

        ExecutorService executor() {
            return executor;
        }
    }

    public static class Info {
        private final String name;
        private final ThreadPoolType type;
        private final int min;
        private final int max;
        private final TimeValue keepAlive;
        private final int queueSize;

        public Info(String name, ThreadPoolType type) {
            this(name, type, -1);
        }

        public Info(String name, ThreadPoolType type, int size) {
            this(name, type, size, size, null, 0);
        }

        public Info(String name, ThreadPoolType type, int min, int max, TimeValue keepAlive, int queueSize) {
            this.name = name;
            this.type = type;
            this.min = min;
            this.max = max;
            this.keepAlive = keepAlive;
            this.queueSize = queueSize;
        }

        public String getName() {
            return this.name;
        }

        public ThreadPoolType getThreadPoolType() {
            return this.type;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public TimeValue getKeepAlive() {
            return this.keepAlive;
        }

        public int getQueueSize() {
            return this.queueSize;
        }

        static final class Fields {
            static final String TYPE = "type";
            static final String MIN = "min";
            static final String MAX = "max";
            static final String KEEP_ALIVE = "keep_alive";
            static final String QUEUE_SIZE = "queue_size";
        }
    }
}
