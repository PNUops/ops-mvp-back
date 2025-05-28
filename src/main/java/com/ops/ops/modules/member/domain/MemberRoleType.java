package com.ops.ops.modules.member.domain;

import lombok.Getter;

@Getter
public enum MemberRoleType {
    ROLE_회원(1),
    ROLE_팀장(2),
    ROLE_관리자(3),
    ;

    private final long id;

    MemberRoleType(final long id) {
        this.id = id;
    }
}
