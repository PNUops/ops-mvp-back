package com.ops.ops.modules.member.application.dto.response;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import java.util.Set;
import lombok.Builder;

@Builder
public record SignInResponse(

        Long memberId,

        String name,

        String token,

        Set<MemberRoleType> types
) {
    public static SignInResponse from(final Member member, final String token) {
        return SignInResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .token(token)
                .types(member.getRoles())
                .build();
    }
}
