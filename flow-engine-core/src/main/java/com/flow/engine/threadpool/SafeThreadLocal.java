package com.flow.engine.threadpool;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定制过的线程池.
 */
class SafeThreadLocal {

    private static final ThreadContextStruct DEFAULT_CONTEXT = new ThreadContextStruct();

    static class ContextThreadLocal extends ClosableThreadLocal<ThreadContextStruct> {
        final AtomicBoolean closed = new AtomicBoolean(false);

        @Override
        public void set(ThreadContextStruct object) {
            try {
                if (object == DEFAULT_CONTEXT) {
                    super.set(null);
                } else {
                    super.set(object);
                }
            } catch (NullPointerException ex) {
            /* This is odd but CloseableThreadLocal throws a NPE if it was closed but still accessed.
               to get a real exception we call ensureOpen() to tell the user we are already closed.*/
                ensureOpen();
                throw ex;
            }
        }

        @Override
        public ThreadContextStruct get() {
            try {
                ThreadContextStruct threadContextStruct = super.get();
                if (threadContextStruct != null) {
                    return threadContextStruct;
                }
                return DEFAULT_CONTEXT;
            } catch (NullPointerException ex) {
            /* This is odd but CloseableThreadLocal throws a NPE if it was closed but still accessed.
               to get a real exception we call ensureOpen() to tell the user we are already closed.*/
                ensureOpen();
                throw ex;
            }
        }

        private void ensureOpen() {
            if (closed.get()) {
                throw new IllegalStateException("threadcontext is already closed");
            }
        }

        @Override
        public void close() {
            if (closed.compareAndSet(false, true)) {
                super.close();
            }
        }
    }

    /**
     * 不同于原生方式，我们定义{@link ThreadContextStruct}只用做一次请求的线程上下文传递
     * <p>
     *     该次请求结束后此变量即作废
     * </p>
     * 参考"org.elasticsearch.common.util.concurrent.ThreadContextStruct"
     */
    static final class ThreadContextStruct {
        final static int DEFAULT_CAPACITY = 4;

        final Map<String, String> requestHeaders;
        final Map<String, List<String>> responseHeaders;

        ThreadContextStruct(Map<String, String> requestHeaders,
                            Map<String, List<String>> responseHeaders) {
            this.requestHeaders = requestHeaders;
            this.responseHeaders = responseHeaders;
        }

        /**
         * This represents the default context and it should only ever be called by {@link #DEFAULT_CONTEXT}.
         */
        ThreadContextStruct() {
            this(Collections.emptyMap(), Collections.emptyMap());
        }

        /**
         * 存放单个上下文
         * @param key
         * @param value
         * @param clear 是否清除已有的内容
         * @return
         */
        ThreadContextStruct putRequest(String key, String value, boolean clear) {
            // 因为不考虑上下文的暂存和恢复，其实可以直接修改实例本身变量，不再新建实例
            // 相对es原生实现，这里做了修改
            ThreadContextStruct struct = this;
            if (isDefault()) {
                struct = new ThreadContextStruct(new HashMap<>(DEFAULT_CAPACITY), responseHeaders);
            }
            if (clear) {
                struct.requestHeaders.clear();
            }
            putSingleHeader(key, value, struct.requestHeaders);
            return struct;
        }

        /**
         * merge headers if absent
         * @param headers
         * @param clear 是否传递上个线程的上下文
         * @return a new {@link ThreadContextStruct}
         */
        ThreadContextStruct putHeaders(Map<String, String> headers, boolean clear) {
            // 相对es原生实现，这里做了修改
            if (headers.isEmpty()) {
                return this;
            } else {
                ThreadContextStruct struct = this;
                if (isDefault()) {
                    struct = new ThreadContextStruct(new HashMap<>(headers.size()), responseHeaders);
                }
                if (clear) {
                    struct.requestHeaders.clear();
                }
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    // 不能用已有的key值覆盖新创建的key（假设存在）
                    putSingleHeader(entry.getKey(), entry.getValue(), struct.requestHeaders);
                }
                // 1. 可以直接修改requestHeaders，因为我们的业务场景只涉及context传递，不涉及stash和restore，只需每次新请求都使用新的Struct容器
                // 2. 因为目前的业务流程只会发生一次put，所以没有直接修改requestHeaders，而是new了一个
                return struct;
            }
        }

        /**
         * 判断header是否存在，存在会抛出异常，不存在则放入header
         * @param key
         * @param value
         * @param newHeaders
         */
        void putSingleHeader(String key, String value, Map<String, String> newHeaders) {
            if (newHeaders.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("value for key [" + key + "] already present");
            }
        }

        ThreadContextStruct putResponse(String key, String value) {
            assert value != null;

            final Map<String, List<String>> newResponseHeaders = new HashMap<>(this.responseHeaders);
            final List<String> existingValues = newResponseHeaders.get(key);

            if (existingValues != null) {
                if (existingValues.contains(value)) {
                    return this;
                }

                final List<String> newValues = new ArrayList<>(existingValues);
                newValues.add(value);

                newResponseHeaders.put(key, Collections.unmodifiableList(newValues));
            } else {
                newResponseHeaders.put(key, Collections.singletonList(value));
            }

            return new ThreadContextStruct(requestHeaders, newResponseHeaders);
        }

        boolean isEmpty() {
            return requestHeaders.isEmpty() && responseHeaders.isEmpty();
        }

        /**
         * 是否默认上下文，默认上下文不能直接存放header，需要实例化一个新的
         * @return
         */
        boolean isDefault() {
            return this == DEFAULT_CONTEXT;
        }

        ThreadContextStruct copyHeaders(Iterable<Map.Entry<String, String>> headers) {
            // 一般来说8个header应该够了吧
            Map<String, String> newHeaders = new HashMap<>(8);
            for (Map.Entry<String, String> header : headers) {
                newHeaders.put(header.getKey(), header.getValue());
            }
            return putHeaders(newHeaders, false);
        }
    }

}
