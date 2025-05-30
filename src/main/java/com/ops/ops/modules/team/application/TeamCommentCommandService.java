package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import com.ops.ops.modules.team.exception.TeamCommentException;
import com.ops.ops.modules.team.exception.TeamCommentExceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamCommentCommandService {

	private final TeamCommandService teamCommandService;
	private final TeamCommentRepository teamCommentRepository;

	@Transactional
	public void createComment(final Long teamId, final Long memberId, final String description) {
		final Team team = teamCommandService.getValidatedTeam(teamId);
		final TeamComment comment = TeamComment.of(description, memberId, team);
		teamCommentRepository.save(comment);
	}

	@Transactional
	public void updateComment(
		final Long teamId,
		final Long commentId,
		final Long memberId,
		final String newDescription
	) {
		teamCommandService.getValidatedTeam(teamId);
		final TeamComment comment = getValidatedCommentOwnedBy(commentId, memberId);
		comment.updateDescription(newDescription);
	}

	@Transactional
	public void deleteComment(
		final Long teamId,
		final Long commentId,
		final Long memberId
	) {
		teamCommandService.getValidatedTeam(teamId);

		final TeamComment comment = getValidatedCommentOwnedBy(commentId, memberId);

		teamCommentRepository.delete(comment);
	}

	private TeamComment getValidatedCommentOwnedBy(final Long commentId, final Long memberId) {
		TeamComment comment = teamCommentRepository.findById(commentId)
			.orElseThrow(() -> new TeamCommentException(TeamCommentExceptionType.NOT_FOUND_COMMENT));

		if (!comment.getMemberId().equals(memberId)) {
			throw new TeamCommentException(TeamCommentExceptionType.NOT_OWNER_COMMENT);
		}

		return comment;
	}
}
