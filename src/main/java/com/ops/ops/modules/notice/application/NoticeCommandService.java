package com.ops.ops.modules.notice.application;

import com.ops.ops.modules.notice.application.convenience.NoticeConvenience;
import com.ops.ops.modules.notice.application.dto.request.NoticeRequest;
import com.ops.ops.modules.notice.domain.Notice;
import com.ops.ops.modules.notice.domain.dao.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;

    private final NoticeConvenience noticeConvenience;

    public void createNotice(final NoticeRequest request) {
        noticeRepository.save(Notice.builder()
                .title(request.title())
                .description(request.description())
                .build());
    }

    public void updateNotice(final NoticeRequest request, final Long noticeId) {
        final Notice notice = noticeConvenience.getValidateExistNotice(noticeId);
        notice.updateNotice(request.title(), request.description());
    }

    public void deleteNotice(final Long noticeId) {
        noticeRepository.delete(noticeConvenience.getValidateExistNotice(noticeId));
    }

}
