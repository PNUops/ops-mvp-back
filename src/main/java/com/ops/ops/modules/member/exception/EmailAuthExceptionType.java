package com.ops.ops.modules.member.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum EmailAuthExceptionType implements BaseExceptionType {

    NOT_VERIFIED_EMAIL_AUTH(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    EmailAuthExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
