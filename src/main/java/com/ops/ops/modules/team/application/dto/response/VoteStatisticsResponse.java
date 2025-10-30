package com.ops.ops.modules.team.application.dto.response;

public record VoteStatisticsResponse(
	int totalVoteCount,
	int votedMemberCount,
	double averageVotePerPerson
) {
}