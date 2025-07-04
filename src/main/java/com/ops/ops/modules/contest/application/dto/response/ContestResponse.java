package com.ops.ops.modules.contest.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ContestResponse(
        Long contestId,
        String contestName,
        @JsonFormat(pattern = "yy.MM.dd HH:mm")
        LocalDateTime updatedAt
) {
}
