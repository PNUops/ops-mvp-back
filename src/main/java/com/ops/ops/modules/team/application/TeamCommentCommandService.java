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
@Transactional
public class TeamCommentCommandService {

	private final TeamCommandService teamCommandService;
	private final TeamCommentRepository teamCommentRepository;

	public void createComment(final Long teamId, final Long memberId, final String description) {
		final Team team = teamCommandService.validateAndGetTeamById(teamId);
		final TeamComment comment = TeamComment.of(description, memberId, team);

		teamCommentRepository.save(comment);
	}

	public void updateComment(final Long teamId, final Long commentId, final Long memberId, final String newDescription) {
		teamCommandService.validateAndGetTeamById(teamId);
		final TeamComment comment = validateAndGetCommentById(commentId);
		validateCommentOwnership(comment, memberId);

		comment.updateDescription(newDescription);
	}

	public void deleteComment(final Long teamId, final Long commentId, final Long memberId) {
		teamCommandService.validateAndGetTeamById(teamId);
		final TeamComment comment = validateAndGetCommentById(commentId);
		validateCommentOwnership(comment, memberId);

		teamCommentRepository.delete(comment);
	}

	private TeamComment validateAndGetCommentById(final Long commentId) {
		return teamCommentRepository.findById(commentId)
			.orElseThrow(() -> new TeamCommentException(TeamCommentExceptionType.NOT_FOUND_COMMENT));
	}

	private void validateCommentOwnership(
		final TeamComment comment,
		final Long memberId
	) {
		if (!comment.getMemberId().equals(memberId)) {
			throw new TeamCommentException(TeamCommentExceptionType.NOT_OWNER_COMMENT);
		}
	}
}
