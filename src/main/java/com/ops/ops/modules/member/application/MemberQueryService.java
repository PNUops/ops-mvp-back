package com.ops.ops.modules.member.application;

import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ops.ops.modules.member.domain.Member;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {
    private final MemberRepository memberRepository;

    public List<String> getMemberNamesByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .map(Member::getName)
                .toList();
    }
    public Long getLeaderIdByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .filter(member -> member.getRoles().contains(MemberRoleType.ROLE_팀장))
                .findFirst()
                .map(Member::getId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_LEADER));
    }
}
