package com.ops.ops.modules.notice.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ops.ops.modules.notice.domain.Notice;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoticeDetailResponse(

        String title,
        String description,
        @JsonFormat(pattern = "yyyy년 MM월 dd일 EEEE HH:mm", locale  = "ko")
        LocalDateTime updatedAt
) {
    public static NoticeDetailResponse from(final Notice notice) {
        return NoticeDetailResponse.builder()
                .title(notice.getTitle())
                .description(notice.getDescription())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
