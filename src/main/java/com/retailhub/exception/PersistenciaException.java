package com.retailhub.exception;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenciaException(String message) {
        super(message);
    }
}
