package com.ops.ops.modules.notice.application;

import com.ops.ops.modules.notice.application.convenience.NoticeConvenience;
import com.ops.ops.modules.notice.application.dto.response.NoticeDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeConvenience noticeConvenience;

    public NoticeDetailResponse getNotice(final Long noticeId) {
        return NoticeDetailResponse.from(noticeConvenience.getValidateExistNotice(noticeId));
    }
}
