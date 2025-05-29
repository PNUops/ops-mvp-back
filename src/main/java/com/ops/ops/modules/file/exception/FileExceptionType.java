package com.ops.ops.modules.file.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FileExceptionType implements BaseExceptionType {
    NO_IMAGE(HttpStatus.BAD_REQUEST, "No image")
    ;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    FileExceptionType(final HttpStatus httpStatus, final String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
