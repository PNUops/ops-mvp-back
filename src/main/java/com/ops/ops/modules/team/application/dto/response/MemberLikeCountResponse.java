package com.ops.ops.modules.team.application.dto.response;

public record MemberLikeCountResponse(
        Long remainingLikeCount,
        Long maxLikeCount
) {
}
