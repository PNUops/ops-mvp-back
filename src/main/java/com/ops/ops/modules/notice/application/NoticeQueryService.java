package com.ops.ops.modules.notice.application;

import com.ops.ops.modules.notice.application.convenience.NoticeConvenience;
import com.ops.ops.modules.notice.application.dto.response.NoticeDetailResponse;
import com.ops.ops.modules.notice.application.dto.response.NoticeSummaryResponse;
import com.ops.ops.modules.notice.domain.dao.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeRepository noticeRepository;

    private final NoticeConvenience noticeConvenience;

    public NoticeDetailResponse getNotice(final Long noticeId) {
        return NoticeDetailResponse.from(noticeConvenience.getValidateExistNotice(noticeId));
    }

    public List<NoticeSummaryResponse> getNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NoticeSummaryResponse::from)
                .toList();
    }
}
