package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TeamSummaryResponse {
    private Long teamId;
    private String teamName;
    private String projectName;
    //    private String thumbnailPath;
    private boolean isLiked;

    public static TeamSummaryResponse from(Team team, boolean isLiked) {
        return TeamSummaryResponse.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .projectName(team.getProjectName())
                .isLiked(isLiked)
                .build();
    }
}
