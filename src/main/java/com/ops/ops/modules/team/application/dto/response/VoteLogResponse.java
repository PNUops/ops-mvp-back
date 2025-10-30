package com.ops.ops.modules.team.application.dto.response;

import java.time.LocalDateTime;

public record VoteLogResponse(
	String voterName,
	String voterEmail,
	String teamName,
	LocalDateTime votedAt
) {
}