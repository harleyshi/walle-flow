package com.flow.engine.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带名称的线程池
 */
public class NamedThreadFactory {

    private static final Logger log = LoggerFactory.getLogger(NamedThreadFactory.class);

    public static ThreadFactory newThreadFactory(String namePrefix) {
        return new PrefixThreadFactory(namePrefix);
    }

    /**
     * 线程名带固定前缀
     */
    public static class PrefixThreadFactory implements ThreadFactory {
        private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private static final String NAME_PATTERN = "%s-%d-thread";
        private final String threadNamePrefix;

        /**
         * Creates a new {@link PrefixThreadFactory} instance
         *
         * @param threadNamePrefix the name prefix assigned to each thread created.
         */
        public PrefixThreadFactory(String threadNamePrefix) {
            final SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            this.threadNamePrefix = String.format(Locale.ROOT, NAME_PATTERN,
                    checkPrefix(threadNamePrefix), threadPoolNumber.getAndIncrement());
        }

        private static String checkPrefix(String prefix) {
            return prefix == null || prefix.length() == 0 ? "EsProxy" : prefix;
        }

        /**
         * Creates a new {@link Thread}
         *
         * @see ThreadFactory#newThread(Runnable)
         */
        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(group, r, String.format(Locale.ROOT, "%s-%d",
                    this.threadNamePrefix, threadNumber.getAndIncrement()), 0);
            t.setUncaughtExceptionHandler((t1, e) -> {
                // print an error log for now
                log.error("thread {} execute job {} failed with uncaught exception {}", t1.getName(), r.toString(), e.getMessage());
            });
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
