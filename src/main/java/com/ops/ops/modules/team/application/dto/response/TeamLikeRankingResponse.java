package com.ops.ops.modules.team.application.dto.response;

public record TeamLikeRankingResponse(
	int rank,
	String teamName,
	String projectName,
	int likeCount
) {
}
