package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;

import java.util.List;

public record TeamDetailResponse(
        Long teamId,
        Long leaderId,
        String teamName,
        String projectName,
        String overview,
        String leaderName,
        List<String> participants,
        List<Long> previewIds,
        String githubPath,
        String youtubePath,
        boolean isLiked
) {
    public static TeamDetailResponse from(
            Team team,
            Long leaderId,
            List<String> participants,
            List<Long> previewIds,
            boolean isLiked
    ) {
        return new TeamDetailResponse(
                team.getId(),
                leaderId,
                team.getTeamName(),
                team.getProjectName(),
                team.getOverview(),
                team.getLeaderName(),
                participants,
                previewIds,
                team.getGithubPath(),
                team.getYouTubePath(),
                isLiked
        );
    }
}