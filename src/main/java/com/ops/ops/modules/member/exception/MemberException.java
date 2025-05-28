package com.ops.ops.modules.member.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;

public class MemberException extends BaseException {

    private final MemberExceptionType exceptionType;

    public MemberException(final MemberExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public MemberException(final MemberExceptionType exceptionType, final String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
