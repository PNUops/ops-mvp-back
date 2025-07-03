package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberCommandService {
    private final TeamCommandService teamCommandService;
    private final TeamMemberRepository teamMemberRepository;

    public void deleteTeamMember(final Long teamId, final Long memberId) {
        teamCommandService.validateAndGetTeamById(teamId);
        final TeamMember teamMember = validateAndGetMemberById(memberId);
        validateCommentOwnership(teamMember, teamId);
        teamMemberRepository.delete(teamMember);
    }

    private TeamMember validateAndGetMemberById(final Long memberId) {
        return teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));
    }

    private void validateCommentOwnership(final TeamMember teamMember, final Long teamId) {
        if (!teamMember.getTeam().getId().equals(teamId)) {
            throw new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER);
        }
    }
}
