package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseExceptionType;
import com.ops.ops.global.base.BaseException;

public class TeamException extends BaseException {

    private final TeamExceptionType exceptionType;

    public TeamException(final TeamExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public TeamException(final TeamExceptionType exceptionType, final String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
