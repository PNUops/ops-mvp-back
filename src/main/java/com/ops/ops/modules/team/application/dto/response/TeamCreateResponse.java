package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;

public record TeamCreateResponse(
        Long teamId
) {
    public static TeamCreateResponse from(
            Team team
    ) {
        return new TeamCreateResponse(
                team.getId()
        );
    }
}
