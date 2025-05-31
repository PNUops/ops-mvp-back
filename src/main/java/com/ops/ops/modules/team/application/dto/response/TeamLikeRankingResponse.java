package com.ops.ops.modules.team.application.dto.response;

public record TeamLikeRankingResponse(
	int rank,
	String teamName,
	String projectName,
	int likeCount
) {
	public static TeamLikeRankingResponse of(int rank, String teamName, String projectName, int likeCount) {
		return new TeamLikeRankingResponse(rank, teamName, projectName, likeCount);
	}
}
