package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TeamExceptionType implements BaseExceptionType {
    NOT_FOUND_TEAM(HttpStatus.NOT_FOUND, "Team not found.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    TeamExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
