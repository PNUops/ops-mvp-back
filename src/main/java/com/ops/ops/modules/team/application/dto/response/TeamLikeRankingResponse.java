package com.ops.ops.modules.team.application.dto.response;

public record TeamLikeRankingResponse(
	int rank,
	String teamName,
	String projectName,
	int likeCount,
	Integer displayOrder,
	String awardTitle,
	String awardBadgeColor,
	String awardBadgeSize
) {
}
