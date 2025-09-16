package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamAdminCommandService {

    private final TeamConvenience teamConvenience;

    public void updateAward(final Long teamId, final String awardName, final String awardColor) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        team.updateAward(awardName, awardColor);
    }
}
