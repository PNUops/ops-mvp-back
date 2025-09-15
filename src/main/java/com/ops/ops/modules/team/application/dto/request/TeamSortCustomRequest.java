package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TeamSortCustomRequest(
        @NotNull
        Long contestId,
        @NotNull @Valid
        List<TeamOrder> teamOrders
) {
    public record TeamOrder(
            @NotNull
            Long teamId,
            @NotNull
            Integer itemOrder
    ) {
    }
}
