package com.flow.engine.exception;

public class FlowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FlowException(String message) {
        super(message);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowException(Throwable cause) {
        super(cause);
    }
}
