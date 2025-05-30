package com.ops.ops.modules.team.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamCommandService {
	private final TeamRepository teamRepository;

	Team getValidatedTeam(final Long teamId) {
		return teamRepository.findById(teamId)
			.orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
	}
}
