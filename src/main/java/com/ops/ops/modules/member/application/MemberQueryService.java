package com.ops.ops.modules.member.application;

import com.ops.ops.modules.member.application.response.LeaderIdResponse;
import com.ops.ops.modules.member.application.response.MemberNameResponse;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {
    private final MemberRepository memberRepository;

    public List<MemberNameResponse> getMemberNamesByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .map(member -> new MemberNameResponse(member.getName()))
                .toList();
    }
    public LeaderIdResponse getLeaderIdByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .filter(member -> member.getRoles().contains(MemberRoleType.ROLE_팀장))
                .findFirst()
                .map(member -> new LeaderIdResponse(member.getId()))
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_LEADER));
    }
}
