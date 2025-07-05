package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
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
public class TeamMemberCommandService {
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final MemberConvenience memberConvenience;
    private final TeamConvenience teamConvenience;

    public void deleteTeamMember(final Long teamId, final Long memberId) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        final Member member = memberConvenience.getValidateExistMember(memberId);
        validateMemberBelongsToTeam(team, memberId);
        removeFakeTeamMemberByName(team, member.getName());
    }

    private void validateMemberBelongsToTeam(final Team team, final Long memberId) {
        teamMemberRepository.findByMemberIdAndTeam(memberId, team)
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));
    }

    public void createTeamMember(final Long teamId, final String newTeamMemberName) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        validateDuplicatedTeamMemberName(team, newTeamMemberName);
        assignFakeTeamMember(team, newTeamMemberName);
    }

    public void removeFakeTeamMemberByName(final Team team, final String teamMemberName) {
        final TeamMember teamMember = findTeamMemberByName(team, teamMemberName);
        teamMemberRepository.delete(teamMember);
        memberRepository.findById(teamMember.getMemberId())
                .filter(Member::isFake)
                .ifPresent(memberRepository::delete);
    }

    public void assignFakeTeamMember(final Team team, final String newTeamMemberName) {
        final Member newMember = memberConvenience.createFakeMember(newTeamMemberName);
        memberRepository.save(newMember);
        final TeamMember newTeamMember = team.addTeamMember(newMember.getId());
        teamMemberRepository.save(newTeamMember);
    }

    public void validateDuplicatedTeamMemberName(Team team, String newMemberName) {
        final boolean duplicated = team.getTeamMembers().stream()
                .anyMatch(tm -> memberRepository.findById(tm.getMemberId())
                        .map(Member::getName)
                        .filter(name -> name.equals(newMemberName))
                        .isPresent());
        if (duplicated) {
            throw new TeamException(TeamExceptionType.DUPLICATED_MEMBER_NAME);
        }
    }

    public TeamMember findTeamMemberByName(Team team, String memberName) {
        final List<Long> memberIds = team.getTeamMembers().stream()
                .map(TeamMember::getMemberId)
                .toList();

        final Map<Long, Member> memberMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        return team.getTeamMembers().stream()
                .filter(tm -> {
                    Member member = memberMap.get(tm.getMemberId());
                    return member != null && member.getName().equals(memberName);
                })
                .findFirst()
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));
    }
}
