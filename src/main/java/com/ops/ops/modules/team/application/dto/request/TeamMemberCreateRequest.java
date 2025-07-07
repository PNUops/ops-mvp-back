package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamMemberCreateRequest(
        @NotBlank(message = "추가할 팀원명은 비어 있을 수 없습니다.")
        String teamMemberName
) {
}
