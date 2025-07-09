package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.team.exception.TeamMemberExceptionType.DUPLICATED_MEMBER_NAME;
import static com.ops.ops.modules.team.exception.TeamMemberExceptionType.NOT_FOUND_TEAM_MEMBER;
import static java.util.stream.Collectors.toMap;

import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.convenience.TeamMemberConvenience;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.exception.TeamMemberException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberCommandService {

    private final TeamMemberRepository teamMemberRepository;

    private final MemberConvenience memberConvenience;
    private final TeamConvenience teamConvenience;
    private final ContestConvenience contestConvenience;
    private final TeamMemberConvenience teamMemberConvenience;

    public void deleteTeamMember(final Long teamId, final Long memberId) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        final Member member = memberConvenience.getValidateExistMember(memberId);
        teamMemberConvenience.validateMemberBelongsToTeam(team, memberId);
        removeFakeTeamMemberByName(team, member.getName());
    }

    public void createTeamMember(final Long teamId, final String newTeamMemberName) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        contestConvenience.validateNotCurrentContest(team.getContestId());
        checkDuplicatedTeamMemberName(team, newTeamMemberName);
        assignFakeTeamMember(team, newTeamMemberName, Set.of(MemberRoleType.ROLE_회원));
    }

    public void removeFakeTeamMemberByName(final Team team, final String teamMemberName) {
        contestConvenience.validateNotCurrentContest(team.getContestId());
        final TeamMember teamMember = findTeamMemberByName(team, teamMemberName);
        teamMemberRepository.delete(teamMember);

        Optional.of(teamMember.getMemberId())
                .map(memberConvenience::getValidateExistMember)
                .filter(Member::isFake)
                .ifPresent(memberConvenience::deleteMember);
    }

    public void assignFakeTeamMember(final Team team, final String newTeamMemberName, final Set<MemberRoleType> roles) {
        final Member newMember = memberConvenience.createFakeMember(newTeamMemberName, roles);
        teamMemberRepository.save(TeamMember.builder()
                .memberId(newMember.getId())
                .team(team)
                .build());
    }

    private void checkDuplicatedTeamMemberName(Team team, String newMemberName) {
        final List<Long> memberIds = team.getTeamMembers().stream()
                .map(TeamMember::getMemberId)
                .toList();

        boolean duplicated = memberConvenience.findAllById(memberIds).stream()
                .map(Member::getName)
                .anyMatch(newMemberName::equals);

        if (duplicated) {
            throw new TeamMemberException(DUPLICATED_MEMBER_NAME);
        }
    }

    private TeamMember findTeamMemberByName(Team team, String memberName) {
        final List<Long> memberIds = team.getTeamMembers().stream()
                .map(TeamMember::getMemberId)
                .toList();

        final Map<Long, Member> memberMap = memberConvenience.findAllById(memberIds)
                .stream()
                .collect(toMap(Member::getId, Function.identity()));

        return team.getTeamMembers().stream()
                .filter(tm -> {
                    Member member = memberMap.get(tm.getMemberId());
                    return member != null && member.getName().equals(memberName);
                })
                .findFirst()
                .orElseThrow(() -> new TeamMemberException(NOT_FOUND_TEAM_MEMBER));
    }
}
