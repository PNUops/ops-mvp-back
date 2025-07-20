package com.ops.ops.modules.team.application.dto.request;

import com.ops.ops.modules.team.domain.SortType;
import jakarta.validation.constraints.NotNull;

public record TeamSortRequest(
        @NotNull(message = "모드를 입력하세요.")
        SortType mode
) {
}
