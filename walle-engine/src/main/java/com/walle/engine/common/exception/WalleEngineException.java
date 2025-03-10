package com.walle.engine.common.exception;

public class WalleEngineException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WalleEngineException(String message) {
        super(message);
    }

    public WalleEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalleEngineException(Throwable cause) {
        super(cause);
    }
}
