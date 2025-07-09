package com.ops.ops.modules.member.application.convenience;

import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_PUSAN_UNIVERSITY_EMAIL;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_EMAIL;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_STUDENT_ID;
import static com.ops.ops.modules.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.EmailAuthException;
import com.ops.ops.modules.member.exception.MemberException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberConvenience {

    final private MemberRepository memberRepository;

    public Member getValidateExistMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public Member getValidateExistMemberByStudentId(final String studentId) {
        return memberRepository.findByStudentId(studentId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public void validateExistMemberByEmail(final String email) {
        memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public Member getValidateExistMemberByEmail(final String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public void validatePusanDomain(final String email) {
        if (!email.endsWith("@pusan.ac.kr")) {
            throw new EmailAuthException(NOT_PUSAN_UNIVERSITY_EMAIL);
        }
    }

    public void checkIsDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ALREADY_EXIST_EMAIL);
        }
    }

    public void checkIsDuplicateStudentId(final String studentId) {
        if (memberRepository.existsByStudentId(studentId)) {
            throw new MemberException(ALREADY_EXIST_STUDENT_ID);
        }
    }

    public Member createFakeMember(final String name, final Set<MemberRoleType> roles) {
        final String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return Member.builder()
                .name(name)
                .studentId("fake_" + unique)
                .email("fake_" + unique + "@placeholder.com")
                .password("!FAKE_USER!")
                .roles(roles)
                .build();
    }

    public long countTotalMember() {
        return memberRepository.count();
    }

    public List<Member> findAllById(final List<Long> memberIds) {
        return memberRepository.findAllById(memberIds);
    }
}
