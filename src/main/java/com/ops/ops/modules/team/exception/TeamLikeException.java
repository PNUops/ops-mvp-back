package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;

public class TeamLikeException extends BaseException {
    private final TeamLikeExceptionType exceptionType;

    public TeamLikeException(final TeamLikeExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
