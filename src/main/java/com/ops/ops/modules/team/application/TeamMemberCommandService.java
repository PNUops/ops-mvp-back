package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
import com.ops.ops.modules.team.domain.Team;
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
    private final MemberRepository memberRepository;

    public void deleteTeamMember(final Long teamId, final Long memberId) {
        final Team team = teamCommandService.validateAndGetTeamById(teamId);
        final TeamMember teamMember = validateAndGetMemberById(memberId);
        validateMemberBelongsToTeam(teamMember, team);
        teamMemberRepository.delete(teamMember);
    }

    private TeamMember validateAndGetMemberById(final Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        return teamMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));
    }

    private void validateMemberBelongsToTeam(final TeamMember teamMember, final Team team) {
        if (!teamMember.getTeam().equals(team)) {
            throw new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER_IN_TEAM);
        }
    }

    public void createTeamMember(final Long teamId, final String newTeamMemberName) {
        final Team team = teamCommandService.validateAndGetTeamById(teamId);
        team.validateDuplicatedMemberName(newTeamMemberName, memberRepository);

        // 가짜 멤버 생성 및 저장
        Member fakeMember = memberRepository.save(Member.createFake(newTeamMemberName));

        // 팀에 추가 및 TeamMember 생성
        TeamMember fakeTeamMember = team.addTeamMember(fakeMember.getId());

        // 새 TeamMember 저장
        teamMemberRepository.save(fakeTeamMember);
    }
}
