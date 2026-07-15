package com.mas.co.exception;

import lombok.Getter;

/**
 * Excepción base para todas las excepciones personalizadas del sistema.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    protected BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    protected BaseException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args != null ? args.clone() : new Object[0];
    }

    protected BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    protected BaseException(String errorCode, String message, Throwable cause, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args != null ? args.clone() : new Object[0];
    }
}