package com.ops.ops.modules.notice.application.dto.response;

import java.time.LocalDateTime;

public record NoticeSummaryResponse(

        Long noticeId,
        String title,
        LocalDateTime createdAt
) {
}
