package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.team.exception.TeamExceptionType.EXIST_ONLY_ONE_ABOUT_TEAM_SORT;

import com.ops.ops.modules.team.domain.TeamSort;
import com.ops.ops.modules.team.domain.dao.TeamSortRepository;
import com.ops.ops.modules.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamSortConvenience {

    private final TeamSortRepository teamSortRepository;

    public void validateExistTeamSort() {
        if (!teamSortRepository.existsById(1L)) {
            teamSortRepository.save(TeamSort.builder().build());
        }
    }

    public TeamSort getValidateExistTeamSort() {
        return teamSortRepository.findById(1L).orElseThrow(() -> new TeamException(EXIST_ONLY_ONE_ABOUT_TEAM_SORT));
    }
}
