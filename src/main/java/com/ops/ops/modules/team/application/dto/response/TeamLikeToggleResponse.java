package com.ops.ops.modules.team.application.dto.response;

public record TeamLikeToggleResponse(
        Long teamId,
        Boolean isLiked,
        String message,
        Long remainingLikeCount,
        Long maxLikeCount
) {
    public static TeamLikeToggleResponse of(Long teamId, Boolean isLiked, String message,
                                            long currentLikeCount, long maxLikeCount) {
        long remainingLikeCount = maxLikeCount - currentLikeCount;
        return new TeamLikeToggleResponse(teamId, isLiked, message, remainingLikeCount, maxLikeCount);
    }
}
