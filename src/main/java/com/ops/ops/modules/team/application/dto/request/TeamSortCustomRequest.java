package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TeamSortCustomRequest(
        @NotNull
        Long contestId,
        @NotNull @Valid
        List<TeamOrder> teamOrders
) {

    @AssertTrue(message = "itemOrder 값이 중복되었습니다.")
    public boolean isDistinctItemOrders() {
        final long distinct = teamOrders.stream()
                .map(TeamOrder::itemOrder)
                .distinct()
                .count();
        return distinct == teamOrders.size();
    }

    public record TeamOrder(
            @NotNull
            Long teamId,
            @NotNull
            Integer itemOrder
    ) {
    }
}
