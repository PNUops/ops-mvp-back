package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;

public record TeamSubmissionStatusResponse(
	Long teamId,
	String teamName,
	String projectName,
	Boolean isSubmitted
) {
	public static TeamSubmissionStatusResponse fromEntity(Team team) {
		return new TeamSubmissionStatusResponse(
			team.getId(),
			team.getTeamName(),
			team.getProjectName(),
			team.getIsSubmitted()
		);
	}
}
