package com.flow.engine.threadpool;

import java.util.Collections;
import java.util.List;

public class ThreadPoolStats {

    private List<Stats> stats;

    public ThreadPoolStats(List<Stats> stats) {
        Collections.sort(stats);
        this.stats = stats;
    }

    public List<Stats> stats() {
        return stats;
    }

    static final class Fields {
        static final String THREAD_POOL = "thread_pool";
        static final String THREADS = "threads";
        static final String QUEUE = "queue";
        static final String ACTIVE = "active";
        static final String REJECTED = "rejected";
        static final String LARGEST = "largest";
        static final String COMPLETED = "completed";
    }

    public static class Stats implements Comparable<Stats> {
        private final String name;
        private final int threads;
        private final int queue;
        private final int active;
        private final long rejected;
        private final int largest;
        private final long completed;

        public Stats(String name, int threads, int queue, int active, long rejected, int largest, long completed) {
            this.name = name;
            this.threads = threads;
            this.queue = queue;
            this.active = active;
            this.rejected = rejected;
            this.largest = largest;
            this.completed = completed;
        }

        public String getName() {
            return this.name;
        }

        public int getThreads() {
            return this.threads;
        }

        public int getQueue() {
            return this.queue;
        }

        public int getActive() {
            return this.active;
        }

        public long getRejected() {
            return rejected;
        }

        public int getLargest() {
            return largest;
        }

        public long getCompleted() {
            return this.completed;
        }

        @Override
        public int compareTo(Stats other) {
            if ((getName() == null) && (other.getName() == null)) {
                return 0;
            } else if ((getName() != null) && (other.getName() == null)) {
                return 1;
            } else if (getName() == null) {
                return -1;
            } else {
                int compare = getName().compareTo(other.getName());
                if (compare == 0) {
                    compare = Integer.compare(getThreads(), other.getThreads());
                }
                return compare;
            }
        }
    }
}
