package com.ops.ops.modules.member.application.convenience;

import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_회원;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
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
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    public Member createFakeMember(final String name) {
        final String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return memberRepository.save(Member.builder()
                .name(name)
                .studentId("fake_" + unique)
                .email("fake_" + unique + "@placeholder.com")
                .password("!FAKE_USER!")
                .roles(Set.of(ROLE_회원))
                .build());
    }
}
