package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.team.domain.Team;
import java.util.List;

public record TeamDetailResponse(
        Long contestId,
        String contestName,
        Long teamId,
        Long leaderId,
        String teamName,
        String projectName,
        String overview,
        String leaderName,
        List<TeamMemberResponse> teamMembers,
        List<Long> previewIds,
        String productionPath,
        String githubPath,
        String youTubePath,
        boolean isLiked
) {
    public static TeamDetailResponse from(
            Contest contest,
            Team team,
            Long leaderId,
            List<TeamMemberResponse> teamMembers,
            List<Long> previewIds,
            boolean isLiked
    ) {
        return new TeamDetailResponse(
                contest.getId(),
                contest.getContestName(),
                team.getId(),
                leaderId,
                team.getTeamName(),
                team.getProjectName(),
                team.getOverview(),
                team.getLeaderName(),
                teamMembers,
                previewIds,
                team.getProductionPath(),
                team.getGithubPath(),
                team.getYouTubePath(),
                isLiked
        );
    }
}
