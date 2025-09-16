package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;

public record TeamSummaryResponse(
        Long teamId,
        String teamName,
        String projectName,
        boolean isLiked,
        String awardName,
        String awardColor) {
    public static TeamSummaryResponse from(Team team, boolean isLiked) {
        return new TeamSummaryResponse(
                team.getId(),
                team.getTeamName(),
                team.getProjectName(),
                isLiked,
                team.getAwardName(),
                team.getAwardColor());
    }
}
