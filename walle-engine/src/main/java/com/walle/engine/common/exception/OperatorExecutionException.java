package com.walle.engine.common.exception;

public class OperatorExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperatorExecutionException(String message) {
        super(message);
    }

    public OperatorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperatorExecutionException(Throwable cause) {
        super(cause);
    }
}
