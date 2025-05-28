package com.ops.ops.global.base;

public abstract class BaseException extends RuntimeException {

    protected BaseException(final String message) {
        super(message);
    }

    public abstract BaseExceptionType exceptionType();
}
