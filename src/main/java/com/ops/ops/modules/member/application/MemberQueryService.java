package com.ops.ops.modules.member.application;

import static com.ops.ops.modules.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.ops.ops.modules.member.application.dto.response.EmailFindResponse;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public EmailFindResponse getMyEmail(final String studentId) {
        final Member member = getValidateMember(studentId);
        return new EmailFindResponse(member.getEmail());
    }

    private Member getValidateMember(final String studentId) {
        return memberRepository.findByStudentId(studentId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
    
}
