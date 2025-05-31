package com.ops.ops.modules.team.application;

import org.springframework.stereotype.Service;

import com.ops.ops.modules.team.application.dto.response.TeamLikeToggleResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamLikeCommandService {

	private final TeamLikeRepository teamLikeRepository;
	private final TeamCommandService teamCommandService;

	public TeamLikeToggleResponse toggleLike(Long memberId, Long teamId, Boolean isLiked) {
		Team team = teamCommandService.validateAndGetTeamById(teamId);
		TeamLike teamLike = getOrCreateTeamLike(memberId, team, isLiked);

		// 좋아요 등록
		if (!teamLike.getIsLiked() && isLiked) {
			teamLike.setLiked(true);
			return new TeamLikeToggleResponse(team.getId(), true, "좋아요가 등록되었습니다.");
		}

		// 좋아요 취소
		if (teamLike.getIsLiked() && !isLiked) {
			teamLike.setLiked(false);
			return new TeamLikeToggleResponse(team.getId(), false, "좋아요가 취소되었습니다.");
		}

		// 상태 변화 없음
		return new TeamLikeToggleResponse(team.getId(), teamLike.getIsLiked(),
			isLiked ? "이미 좋아요한 팀입니다." : "이미 좋아요를 취소한 팀입니다.");
	}

	private TeamLike getOrCreateTeamLike(Long memberId, Team team, Boolean isLiked) {
		return teamLikeRepository.findByMemberIdAndTeam(memberId, team)
			.orElseGet(() -> createTeamLike(memberId, team, isLiked));
	}

	public TeamLike createTeamLike(Long memberId, Team team, Boolean isLiked) {
		return teamLikeRepository.save(TeamLike.of(memberId, team, isLiked));
	}
}
