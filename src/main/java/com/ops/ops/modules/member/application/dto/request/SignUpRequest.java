package com.ops.ops.modules.member.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record SignUpRequest(

        @NotNull(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "학번을 입력해주세요.")
        String studentId,

        @NotNull(message = "부산대 이메일을 입력해주세요.")
        String email,

        @NotNull(message = "비밀번호를 입력해주세요.")
        String password
) {
}
