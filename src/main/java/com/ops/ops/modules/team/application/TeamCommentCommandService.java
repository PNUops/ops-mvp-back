package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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
}
