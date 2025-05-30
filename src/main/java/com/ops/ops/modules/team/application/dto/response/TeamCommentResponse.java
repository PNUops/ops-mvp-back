package com.ops.ops.modules.team.application.dto.response;

import com.ops.ops.modules.team.domain.TeamComment;

public record TeamCommentResponse(
	Long commentId,
	String description,
	Long memberId,
	Long teamId
) {
	public static TeamCommentResponse from(TeamComment comment) {
		return new TeamCommentResponse(
			comment.getId(),
			comment.getDescription(),
			comment.getMemberId(),
			comment.getTeam().getId()
		);
	}
}
