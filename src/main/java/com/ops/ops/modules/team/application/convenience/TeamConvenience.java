package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamConvenience {
    private final TeamRepository teamRepository;

    public Team getValidateExistTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    public void validateExistTeam(Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}
