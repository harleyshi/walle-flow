package com.flow.engine.threadpool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程池类型.
 */
public enum ThreadPoolType {

    /**
     * 在调用线程中运行
     */
    DIRECT("direct"),

    /**
     * 固定大小线程池
     */
    FIXED("fixed"),

    /**
     * 自动伸缩线程池
     */
    SCALING("scaling");

    private final String type;

    public String getType() {
        return type;
    }

    ThreadPoolType(String type) {
        this.type = type;
    }

    private static final Map<String, ThreadPoolType> TYPE_MAP;

    static {
        Map<String, ThreadPoolType> typeMap = new HashMap<>();
        for (ThreadPoolType threadPoolType : ThreadPoolType.values()) {
            typeMap.put(threadPoolType.getType(), threadPoolType);
        }
        TYPE_MAP = Collections.unmodifiableMap(typeMap);
    }

    public static ThreadPoolType fromType(String type) {
        ThreadPoolType threadPoolType = TYPE_MAP.get(type);
        if (threadPoolType == null) {
            throw new IllegalArgumentException("no ThreadPoolType for " + type);
        }
        return threadPoolType;
    }

}
