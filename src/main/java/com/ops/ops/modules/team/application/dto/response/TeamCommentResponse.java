package com.ops.ops.modules.team.application.dto.response;

public record TeamCommentResponse(
	Long commentId,
	String description,
	Long memberId,
	String memberName,
	Long teamId
) {
}
