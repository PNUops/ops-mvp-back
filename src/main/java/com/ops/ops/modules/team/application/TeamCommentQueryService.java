package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.application.dto.response.TeamCommentResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamCommentQueryService {

	private final TeamCommentRepository teamCommentRepository;
	private final TeamCommandService teamCommandService;
	private final MemberRepository memberRepository;

	public List<TeamCommentResponse> getComments(final Long teamId) {
		Team team = teamCommandService.validateAndGetTeamById(teamId);
		List<TeamComment> comments = teamCommentRepository.findAllByTeamId(team.getId());

		return comments.stream()
			.map(comment -> {
				String memberName = memberRepository.findNameById(comment.getMemberId());
				return new TeamCommentResponse(
					comment.getId(),
					comment.getDescription(),
					comment.getMemberId(),
					memberName,
					team.getId()
				);
			})
			.toList();
	}
}
