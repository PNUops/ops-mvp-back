package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.application.MemberQueryService;
import com.ops.ops.modules.member.application.response.MemberNameResponse;
import com.ops.ops.modules.member.application.response.LeaderIdResponse;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamMemberQueryService {

    private final TeamMemberRepository teamMemberRepository;
    private final MemberQueryService memberQueryService;

    public List<String> getParticipantsbyTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);
        List<Long> memberIds = participants.stream()
                .map(TeamMember::getMemberId)
                .toList();

        return memberQueryService.getMemberNamesByIds(memberIds).stream()
                .map(MemberNameResponse::getName)
                .toList();
    }
    public Long getLeaderIdByTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);

        List<Long> memberIds = participants.stream()
                .map(TeamMember::getMemberId)
                .toList();
        return memberQueryService.getLeaderIdByIds(memberIds).getLeaderId();
    }
}
