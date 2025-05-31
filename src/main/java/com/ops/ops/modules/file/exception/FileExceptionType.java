package com.ops.ops.modules.file.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FileExceptionType implements BaseExceptionType {
    NO_IMAGE(HttpStatus.BAD_REQUEST, "요청에 이미지가 없습니다"),
    EXCEED_PREVIEW_LIMIT(HttpStatus.BAD_REQUEST, "프리뷰 이미지는 6장 이하입니다")
    ;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    FileExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
