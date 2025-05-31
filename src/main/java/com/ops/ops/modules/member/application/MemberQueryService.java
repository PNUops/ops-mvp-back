package com.ops.ops.modules.member.application;

import com.ops.ops.modules.member.application.response.MemberNameResponse;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
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
}
