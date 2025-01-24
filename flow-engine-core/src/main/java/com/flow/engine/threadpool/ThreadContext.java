package com.flow.engine.threadpool;



import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flow.engine.threadpool.SafeThreadLocal.*;

/**
 * 线程上下文，用于多线程间传递调用上下文.
 * <br>
 *     目前就支持进程内传递，原生的es context还可以在网络间传递，参考"org.elasticsearch.common.util.concurrent.ThreadContext",
 *     拆分了一部分代码到 {@link SafeThreadLocal}
 * <p>
 *
 * @author copied from es
 * Created by maoxiajun on 2019-02-11.
 */
public class ThreadContext implements Closeable {

    public static final String PREFIX = "request.headers.";
    /**
     * 用来存储默认请求头，这里暂时先放个空
     */
    public static final Settings DEFAULT_HEADERS_SETTING = Settings.EMPTY;
    private static final ThreadContextStruct DEFAULT_CONTEXT = new ThreadContextStruct();
    private final Map<String, String> defaultHeader;
    private final ContextThreadLocal threadLocal;

    /**
     * Creates a new ThreadContext instance
     * @param settings the settings to read the default request headers from
     */
    public ThreadContext(Settings settings) {
        Settings headers = DEFAULT_HEADERS_SETTING.getByPrefix(PREFIX);
        if (headers == null) {
            this.defaultHeader = Collections.emptyMap();
        } else {
            Map<String, String> defaultHeader = new HashMap<>();
            for (String key : headers.names()) {
                defaultHeader.put(key, headers.get(key));
            }
            this.defaultHeader = Collections.unmodifiableMap(defaultHeader);
        }
        threadLocal = new ContextThreadLocal();
    }

    @Override
    public void close() throws IOException {
        threadLocal.close();
    }

    /**
     * Removes the current context and resets a default context. The removed context can be
     * restored when closing the returned {@link StoredContext}
     */
    public StoredContext stashContext() {
        // 1. 记录上下文
        final ThreadContextStruct context = threadLocal.get();
        threadLocal.set(null);
        // 2. 在lambda中回调恢复1记录的上下文
        return () -> threadLocal.set(context);
    }

    /**
     * Just like {@link #stashContext()} but no default context is set.
     */
    public StoredContext newStoredContext() {
        // 1. 先记录当时的上下文
        final ThreadContextStruct context = threadLocal.get();
        // 2. 返回一个lambda，在任务运行结束时用来恢复1记录的上下文
        return () -> threadLocal.set(context);
    }

    /**
     * Returns the header for the given key or <code>null</code> if not present
     */
    public String getHeader(String key) {
        String value = threadLocal.get().requestHeaders.get(key);
        if (value == null)  {
            return defaultHeader.get(key);
        }
        return value;
    }

    /**
     * Returns all of the request contexts headers
     */
    public Map<String, String> getHeaders() {
        HashMap<String, String> map = new HashMap<>(defaultHeader);
        map.putAll(threadLocal.get().requestHeaders);
        return Collections.unmodifiableMap(map);
    }

    /**
     * Get a copy of all <em>response</em> headers.
     *
     * @return Never {@code null}.
     */
    public Map<String, List<String>> getResponseHeaders() {
        Map<String, List<String>> responseHeaders = threadLocal.get().responseHeaders;
        HashMap<String, List<String>> map = new HashMap<>(responseHeaders.size());

        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            map.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }

        return Collections.unmodifiableMap(map);
    }

    /**
     * Copies all header key, value pairs into the current context
     */
    public void copyHeaders(Iterable<Map.Entry<String, String>> headers) {
        threadLocal.set(threadLocal.get().copyHeaders(headers));
    }

    /**
     * Puts a header into the context
     * @param clear 是否清除或覆盖已存在的上下文
     */
    public void putHeader(String key, String value, boolean clear) {
        threadLocal.set(threadLocal.get().putRequest(key, value, clear));
    }

    /**
     * Puts all of the given headers into this context
     * <P>
     *     注意这里如果put的header和已存在的header的key重复，\不会\发生覆盖
     * </P>
     * @param clear 是否清除已存在的header
     */
    public void putHeader(Map<String, String> header, boolean clear) {
        threadLocal.set(threadLocal.get().putHeaders(header, clear));
    }

    /**
     * Add the <em>unique</em> response {@code value} for the specified {@code key}.
     * <p>
     * Any duplicate {@code value} is ignored.
     */
    public void addResponseHeader(String key, String value) {
        threadLocal.set(threadLocal.get().putResponse(key, value));
    }

    /**
     * Saves the current thread context and wraps command in a Runnable that restores that context before running command. If
     * <code>command</code> has already been passed through this method then it is returned unaltered rather than wrapped twice.
     */
    public Runnable preserveContext(Runnable command) {
        if (command instanceof ContextPreservingAbstractRunnable) {
            return command;
        }
        if (command instanceof ContextPreservingRunnable) {
            return command;
        }
        // 这里构造线程的时候就顺带保存了上下文
        if (command instanceof SafeRunnable) {
            return new ContextPreservingAbstractRunnable((SafeRunnable) command);
        }
        return new ContextPreservingRunnable(command);
    }

    /**
     * Unwraps a command that was previously wrapped by {@link #preserveContext(Runnable)}.
     */
    public Runnable unwrap(Runnable command) {
        if (command instanceof ContextPreservingAbstractRunnable) {
            return ((ContextPreservingAbstractRunnable) command).unwrap();
        }
        if (command instanceof ContextPreservingRunnable) {
            return ((ContextPreservingRunnable) command).unwrap();
        }
        return command;
    }

    /**
     * Returns true if the current context is the default context.
     */
    boolean isDefaultContext() {
        return threadLocal.get().isDefault();
    }

    /**
     * Returns <code>true</code> if the context is closed, otherwise <code>false</code>
     */
    boolean isClosed() {
        return threadLocal.closed.get();
    }

    /**
     * Returns <code>true</code> if the context is empty, otherwise <code>false</code>
     */
    boolean isEmpty() {
        return threadLocal.get().isEmpty();
    }

    @FunctionalInterface
    public interface StoredContext extends AutoCloseable {
        @Override
        void close();

        default void restore() {
            close();
        }
    }

    /**
     * 用于包装普通的{@link Runnable}
     */
    class ContextPreservingRunnable implements Runnable {
        private final Runnable in;
        private final StoredContext ctx;

        private ContextPreservingRunnable(Runnable in) {
            // 先缓存producer线程的context，注意，这里是异步的，下面的run方法会在consumer线程中运行
            // producer和consumer对应这个runnable的生产者和实际运行者，暂且这么叫吧
//            ctx = newStoredContext();
            // change log 20190228，不需要保留当前线程的上下文，只需要传递给下一个线程
            ctx = stashContext();
            this.in = in;
        }

        @Override
        public void run() {
            boolean whileRunning = false;
            // 当真正执行的时候缓存consumer线程的context
//            try (ThreadContext.StoredContext ignore = stashContext()) {
            // 改为一个普通try，这里不需要保留并还原工作线程的上下文
            try {
                // 再恢复producer线程的context
                ctx.restore();
                whileRunning = true;
                // 在producer上下文中运行
                in.run();
                whileRunning = false;
            } catch (IllegalStateException ex) {
                if (whileRunning || threadLocal.closed.get() == false) {
                    throw ex;
                }
                // if we hit an ISE here we have been shutting down
                // this comes from the threadcontext and barfs if
                // our threadpool has been shutting down
            }
            // 因为用了try with resource，在try代码块执行结束会自动运行 StoredContext#close , 也就恢复了consumer线程的上下文
        }

        @Override
        public String toString() {
            return in.toString();
        }

        public Runnable unwrap() {
            return in;
        }
    }

    /**
     * 用于包装{@link SafeRunnable}
     */
    class ContextPreservingAbstractRunnable extends SafeRunnable {
        private final SafeRunnable in;
        private final StoredContext creatorsContext;

        // 不需要暂存工作线程的上下文，所以都注释掉了
//        private ThreadContext.StoredContext threadsOriginalContext = null;

        private ContextPreservingAbstractRunnable(SafeRunnable in) {
            // 构造器，先暂存producer线程的上下文
//            creatorsContext = newStoredContext();
            // change log 20190228，不需要保留当前线程的上下文，只需要传递给下一个线程
            creatorsContext = stashContext();
            this.in = in;
        }

        @Override
        public boolean isForceExecution() {
            return in.isForceExecution();
        }

        @Override
        public void onAfter() {
            try {
                in.onAfter();
            } finally {
                // 执行结束，恢复consumer线程的上下文
//                if (threadsOriginalContext != null) {
//                    threadsOriginalContext.restore();
//                }
            }
        }

        @Override
        public void onFailure(Exception e) {
            in.onFailure(e);
        }

        @Override
        public void onRejection(Exception e) {
            in.onRejection(e);
        }

        @Override
        protected void doRun() throws Exception {
            boolean whileRunning = false;
            // 缓存调用者consumer线程的上下文
//            threadsOriginalContext = stashContext();
            try {
                // 恢复producer的上下文
                creatorsContext.restore();
                whileRunning = true;
                // 在producer上下文下运行
                in.doRun();
                whileRunning = false;
            } catch (IllegalStateException ex) {
                if (whileRunning || threadLocal.closed.get() == false) {
                    throw ex;
                }
                // if we hit an ISE here we have been shutting down
                // this comes from the threadcontext and barfs if
                // our threadpool has been shutting down
            }
        }

        @Override
        public String toString() {
            return in.toString();
        }

        public SafeRunnable unwrap() {
            return in;
        }
    }
}
