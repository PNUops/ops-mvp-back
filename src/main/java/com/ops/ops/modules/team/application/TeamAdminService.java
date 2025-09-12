package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.dto.request.TeamAwardUpdateRequest;
import com.ops.ops.modules.team.application.dto.request.TeamDisplayOrderRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamAdminService {

	private final TeamRepository teamRepository;

	public void updateTeamAward(Long teamId, TeamAwardUpdateRequest request) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
		
		team.updateAwardInfo(
			request.awardTitle(),
			request.awardBadgeColor(),
			request.awardBadgeSize(),
			request.displayOrder()
		);
	}

	public void updateDisplayOrders(List<TeamDisplayOrderRequest> requests) {
		List<Long> teamIds = requests.stream()
			.map(TeamDisplayOrderRequest::teamId)
			.toList();
		
		List<Team> teams = teamRepository.findAllById(teamIds);
		
		Map<Long, Team> teamMap = teams.stream()
			.collect(Collectors.toMap(Team::getId, Function.identity()));
		
		for (TeamDisplayOrderRequest request : requests) {
			Team team = teamMap.get(request.teamId());
			if (team != null) {
				team.updateDisplayOrder(request.displayOrder());
			}
		}
	}
}