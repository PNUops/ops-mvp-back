package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TeamDetailResponse {
    private Long teamId;
    private Long leaderId;
    private String teamName;
    private String projectName;
    private String overview;
    private String leaderName;
    private List<String> participants;
//    private List<String> imagePath;
    private String githubPath;
    private String youtubePath;
    private boolean isLiked;

    public static TeamDetailResponse from(Team team, Long leaderId, List<String> participants, boolean isLiked) {
        return TeamDetailResponse.builder()
                .teamId(team.getId())
                .leaderId(leaderId)
                .teamName(team.getTeamName())
                .projectName(team.getProjectName())
                .overview(team.getOverview())
                .leaderName(team.getLeaderName())
                .participants(participants)
//                .imagePath(team.getFiles().stream()
//                        .map(TeamFile::getFilePath)
//                        .collect(Collectors.toList()))
                .githubPath(team.getGithubPath())
                .youtubePath(team.getYouTubePath())
                .isLiked(isLiked)
                .build();
    }


}
