package com.ops.ops.modules.team.exception;

import com.ops.ops.global.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TeamExceptionType implements BaseExceptionType {
    NOT_FOUND_TEAM(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."),
    NOT_TEAM_LEADER(HttpStatus.FORBIDDEN, "해당 팀의 팀장 권한이 없습니다."),
    NOT_FOUND_TEAM_MEMBER(HttpStatus.NOT_FOUND, "팀원을 찾을 수 없습니다."),
    NOT_FOUND_TEAM_MEMBER_IN_TEAM(HttpStatus.NOT_FOUND, "해당 팀에 속해있지 않습니다."),
    DUPLICATED_MEMBER_NAME(HttpStatus.CONFLICT, "팀 내에 동일한 이름을 가진 팀원이 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorMessage;

    TeamExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
