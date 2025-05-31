package com.ops.ops.modules.member.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum EmailAuthExceptionType implements BaseExceptionType {

    NOT_VERIFIED_EMAIL_AUTH(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    NOT_PUSAN_UNIVERSITY_EMAIL(HttpStatus.BAD_REQUEST, "부산대 이메일만 가입 가능합니다."),
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
