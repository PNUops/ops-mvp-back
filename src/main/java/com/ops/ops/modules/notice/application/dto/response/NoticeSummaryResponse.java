package com.ops.ops.modules.notice.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ops.ops.modules.notice.domain.Notice;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoticeSummaryResponse(

        Long noticeId,
        String title,
        @JsonFormat(pattern = "yyyy년 MM월 dd일 EEEE HH:mm", locale  = "ko")
        LocalDateTime updatedAt
) {
    public static NoticeSummaryResponse from(final Notice notice) {
        return NoticeSummaryResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
