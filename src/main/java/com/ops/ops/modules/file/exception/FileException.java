package com.ops.ops.modules.file.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;
import com.ops.ops.modules.member.exception.MemberExceptionType;

public class FileException extends BaseException {

    private final FileExceptionType exceptionType;

    public FileException(final FileExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public FileException(final FileExceptionType exceptionType, final String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
