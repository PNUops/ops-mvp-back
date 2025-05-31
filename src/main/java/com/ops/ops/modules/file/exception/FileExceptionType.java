package com.ops.ops.modules.file.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FileExceptionType implements BaseExceptionType {
    NO_IMAGE(HttpStatus.BAD_REQUEST, "요청에 이미지가 없습니다"),
    NOT_EXISTS_PREVIEW(HttpStatus.NOT_FOUND, "존재하지 않는 프리뷰 이미지입니다"),
    REQUEST_NOT_OWN_IMAGE(HttpStatus.BAD_REQUEST, "자신의 것이 아닌 이미지를 요청하였습니다.")
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
