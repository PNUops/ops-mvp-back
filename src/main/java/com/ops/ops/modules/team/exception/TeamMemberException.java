package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;

public class TeamMemberException extends BaseException {

    private final TeamMemberExceptionType exceptionType;

    public TeamMemberException(final TeamMemberExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public TeamMemberException(final TeamMemberExceptionType exceptionType, final String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
