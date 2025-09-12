package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.Team;

public record TeamSubmissionStatusResponse(
	Long teamId,
	String teamName,
	String projectName,
	Boolean isSubmitted,
	Integer displayOrder,
	String awardTitle,
	String awardBadgeColor,
	String awardBadgeSize
) {
	public static TeamSubmissionStatusResponse fromEntity(Team team) {
		return new TeamSubmissionStatusResponse(
			team.getId(),
			team.getTeamName(),
			team.getProjectName(),
			team.getIsSubmitted(),
			team.getDisplayOrder(),
			team.getAwardTitle(),
			team.getAwardBadgeColor(),
			team.getAwardBadgeSize()
		);
	}
}
