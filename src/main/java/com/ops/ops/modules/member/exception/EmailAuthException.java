package com.ops.ops.modules.member.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;

public class EmailAuthException extends BaseException {

    private final EmailAuthExceptionType exceptionType;

    public EmailAuthException(final EmailAuthExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public EmailAuthException(final EmailAuthExceptionType exceptionType, final String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
