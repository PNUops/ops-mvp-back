package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import java.util.Optional;

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

	private final TeamConvenience teamConvenience;

	public TeamLikeToggleResponse toggleLike(Long memberId, Long teamId, Boolean isLiked) {
		Team team = teamConvenience.getValidateExistTeam(teamId);

		Optional<TeamLike> teamLikeOptional = teamLikeRepository.findByMemberIdAndTeam(memberId, team);
		if (teamLikeOptional.isEmpty()) {
			saveTeamLike(memberId, team, isLiked);
			String message = isLiked ? "좋아요가 처음 등록되었습니다." : "좋아요가 비활성화된 상태로 초기화되었습니다.";
			return new TeamLikeToggleResponse(team.getId(), isLiked, message);
		}

		TeamLike teamLike = teamLikeOptional.get();
		if (teamLike.getIsLiked() == isLiked) {
			String message = isLiked ? "이미 좋아요한 팀입니다." : "이미 좋아요를 취소한 팀입니다.";
			return new TeamLikeToggleResponse(team.getId(), teamLike.getIsLiked(), message);
		}

		teamLike.setLiked(isLiked);
		String message = isLiked ? "좋아요가 등록되었습니다." : "좋아요가 취소되었습니다.";
		return new TeamLikeToggleResponse(team.getId(), isLiked, message);
	}

	private void saveTeamLike(Long memberId, Team team, Boolean isLiked) {
		TeamLike teamLike = TeamLike.of(memberId, team, isLiked);
		teamLikeRepository.save(teamLike);
	}
}
