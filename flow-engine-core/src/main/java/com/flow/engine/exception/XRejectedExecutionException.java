package com.flow.engine.exception;

/**
 * 线程池满拒绝请求.
 */
public class XRejectedExecutionException extends RuntimeException {

    private final boolean isExecutorShutdown;

    public XRejectedExecutionException(String message) {
        this(message, false);
    }

    public XRejectedExecutionException(String message, boolean isExecutorShutdown) {
        super(message);
        this.isExecutorShutdown = isExecutorShutdown;
    }

    public XRejectedExecutionException(Exception e) {
        super(null, e);
        isExecutorShutdown = false;
    }

}
