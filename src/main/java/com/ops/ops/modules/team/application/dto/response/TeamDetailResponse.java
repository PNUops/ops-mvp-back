package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TeamDetailResponse {
    private Long teamId;
//    private Long leaderId;
    private String teamName;
    private String projectName;
    private String overview;
    private String leaderName;
//    private List<String> participants;
//    private List<String> imagePath;
    private String githubPath;
    private String youtubePath;
    private boolean isLiked;

    public static TeamDetailResponse from(Team team, boolean isLiked) {
        return TeamDetailResponse.builder()
                .teamId(team.getId())
//                .leaderId(team.getLeader().getId())
                .teamName(team.getTeamName())
                .projectName(team.getProjectName())
                .overview(team.getOverview())
                .leaderName(team.getLeaderName())
//                .participants(team.getParticipants().stream())
//                        .map(Member::getName)
//                        .collect(Collectors.toList()))
//                .imagePath(team.getFiles().stream()
//                        .map(TeamFile::getFilePath)
//                        .collect(Collectors.toList()))
                .githubPath(team.getGithubPath())
                .youtubePath(team.getYouTubePath())
                .isLiked(isLiked)
                .build();
    }


}
