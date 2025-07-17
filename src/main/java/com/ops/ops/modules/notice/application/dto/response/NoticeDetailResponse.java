package com.ops.ops.modules.notice.application.dto.response;

import java.time.LocalDateTime;

public record NoticeDetailResponse(

        String title,
        String description,
        LocalDateTime updatedAt
) {
}
