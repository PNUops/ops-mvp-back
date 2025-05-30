package com.ops.ops.modules.team.application.dto.response;

public record TeamSubmissionStatusResponse(
	Long teamId,
	String teamName,
	String projectName,
	Boolean isSubmitted
) {
	public static TeamSubmissionStatusResponse fromEntity(com.ops.ops.modules.team.domain.Team team) {
		return new TeamSubmissionStatusResponse(
			team.getId(),
			team.getTeamName(),
			team.getProjectName(),
			team.getIsSubmitted()
		);
	}
}
