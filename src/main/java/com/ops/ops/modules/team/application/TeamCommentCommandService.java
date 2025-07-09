package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.team.exception.TeamCommentExceptionType.NOT_OWNER_COMMENT;

import com.ops.ops.modules.team.application.convenience.TeamCommentConvenience;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import com.ops.ops.modules.team.exception.TeamCommentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommentCommandService {

	private final TeamCommentRepository teamCommentRepository;

	private final TeamConvenience teamConvenience;
	private final TeamCommentConvenience teamCommentConvenience;

	public void createComment(final Long teamId, final Long memberId, final String description) {
		final Team team = teamConvenience.getValidateExistTeam(teamId);
		final TeamComment comment = TeamComment.of(description, memberId, team);

		teamCommentRepository.save(comment);
	}

	public void updateComment(final Long teamId, final Long commentId, final Long memberId, final String newDescription) {
		teamConvenience.getValidateExistTeam(teamId);
		final TeamComment comment = teamCommentConvenience.getValidateExistComment(commentId);
		isMine(comment, memberId);

		comment.updateDescription(newDescription);
	}

	public void deleteComment(final Long teamId, final Long commentId, final Long memberId) {
		teamConvenience.validateExistTeam(teamId);
		final TeamComment comment = teamCommentConvenience.getValidateExistComment(commentId);
		isMine(comment, memberId);

		teamCommentRepository.delete(comment);
	}

	private void isMine(final TeamComment comment, final Long memberId) {
		if (!comment.isMine(memberId)) {
			throw new TeamCommentException(NOT_OWNER_COMMENT);
		}
	}
}
