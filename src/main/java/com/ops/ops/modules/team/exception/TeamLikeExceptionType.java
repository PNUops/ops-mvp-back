package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TeamLikeExceptionType implements BaseExceptionType {
    LIKE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "대회당 최대 2개 팀만 좋아요할 수 있습니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 팀입니다."),
    ALREADY_UNLIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 취소한 팀입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    TeamLikeExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
