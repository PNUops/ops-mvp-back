package com.ops.ops.global.security;

import static com.ops.ops.modules.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        final Member member = memberRepository.findByEmail(email)
                .filter(m -> !m.getIsDeleted())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return new MemberDetails(
                member.getId(),
                member.getName(),
                member.getPassword(),
                member.getRoles().stream()
                        .map(MemberRoleType::name)
                        .toList());
    }
}
