package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.team.exception.TeamMemberExceptionType.NOT_FOUND_TEAM_MEMBER;

import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.exception.TeamMemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberConvenience {

    private final TeamMemberRepository teamMemberRepository;

    public void validateMemberBelongsToTeam(final Team team, final Long memberId) {
        teamMemberRepository.findByMemberIdAndTeam(memberId, team)
                .orElseThrow(() -> new TeamMemberException(NOT_FOUND_TEAM_MEMBER));
    }

    public List<Long> getTeamMemberIdsByTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);

        return participants.stream()
                .map(TeamMember::getMemberId)
                .toList();
    }
}
