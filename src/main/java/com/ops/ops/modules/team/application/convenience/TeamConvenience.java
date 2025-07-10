package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.contest.exception.ContestExceptionType.CONTEST_HAS_TEAMS;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import java.util.List;
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

    public void checkAllContestDelete(final Long contestId) {
        if (teamRepository.existsByContestId(contestId)) {
            throw new ContestException(CONTEST_HAS_TEAMS);
        }
    }

    public List<Team> findAllByContestId(final Long contestId) {
        return teamRepository.findAllByContestId(contestId);
    }

}
