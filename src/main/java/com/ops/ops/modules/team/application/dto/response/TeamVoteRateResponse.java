package com.ops.ops.modules.team.application.dto.response;

public record TeamVoteRateResponse(
	Double voteRate,
	Integer totalVoteCount
) {}
