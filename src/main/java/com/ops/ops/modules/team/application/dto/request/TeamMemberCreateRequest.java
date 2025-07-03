package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamMemberCreateRequest(
        @NotBlank String teamMemberName
) {
}
