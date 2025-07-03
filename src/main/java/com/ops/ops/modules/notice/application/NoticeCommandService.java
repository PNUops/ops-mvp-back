package com.ops.ops.modules.notice.application;

import static com.ops.ops.modules.notice.exception.NoticeExceptionType.NOT_FOUND_NOTICE;

import com.ops.ops.modules.notice.application.dto.request.NoticeRequest;
import com.ops.ops.modules.notice.domain.Notice;
import com.ops.ops.modules.notice.domain.dao.NoticeRepository;
import com.ops.ops.modules.notice.exception.NoticeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;

    public void createNotice(final NoticeRequest request) {
        noticeRepository.save(Notice.builder()
                .title(request.title())
                .description(request.description())
                .build());
    }

    public void updateNotice(final NoticeRequest request, final Long noticeId) {
        final Notice notice = getValidateExistNotice(noticeId);
        notice.updateNotice(request.title(), request.description());
    }

    private Notice getValidateExistNotice(final Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(NOT_FOUND_NOTICE));
    }
}
