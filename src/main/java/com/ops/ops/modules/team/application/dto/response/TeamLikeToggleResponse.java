package com.ops.ops.modules.team.application.dto.response;

public record TeamLikeToggleResponse(
	Long teamId,
	Boolean isLiked,
	String message
) {
}
