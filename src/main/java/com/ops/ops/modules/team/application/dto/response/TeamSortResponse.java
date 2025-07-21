package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.SortType;

public record TeamSortResponse(

        SortType currentMode
) {
}
