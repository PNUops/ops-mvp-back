package com.ops.ops.modules.contest.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ContestCreateRequest(
        @NotBlank(message = "추가할 대회명은 비어 있을 수 없습니다.")
        String contestName
) {
}
