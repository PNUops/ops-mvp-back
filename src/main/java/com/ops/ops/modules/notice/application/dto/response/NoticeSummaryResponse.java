package com.ops.ops.modules.notice.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record NoticeSummaryResponse(

        Long noticeId,
        String title,
        @JsonFormat(pattern = "yyyy년 MM월 dd일 EEEE HH:mm", locale  = "ko")
        LocalDateTime updatedAt
) {
}
