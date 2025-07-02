package com.ops.ops.modules.contest.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ContestExceptionType implements BaseExceptionType {
    NOT_FOUND_CONTEST(HttpStatus.NOT_FOUND, "존재하지 않는 대회입니다.");
    
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ContestExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
