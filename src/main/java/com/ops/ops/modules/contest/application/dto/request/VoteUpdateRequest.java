package com.ops.ops.modules.contest.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public record VoteUpdateRequest(
        @NotEmpty(message = "투표 시작 시각을 정해야 합니다")
        LocalDateTime voteStartAt,
        @NotEmpty(message = "투표 종료 시각을 정해야 합니다")
        LocalDateTime voteEndAt
) {
}
