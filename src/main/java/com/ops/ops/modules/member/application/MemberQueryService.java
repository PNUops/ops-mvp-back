package com.ops.ops.modules.member.application;

import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.application.dto.response.EmailFindResponse;
import com.ops.ops.modules.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberConvenience memberConvenience;

    public EmailFindResponse getMyEmail(final String studentId) {
        final Member member = memberConvenience.getValidateExistMemberByStudentId(studentId);
        return new EmailFindResponse(member.getEmail());
    }

}
