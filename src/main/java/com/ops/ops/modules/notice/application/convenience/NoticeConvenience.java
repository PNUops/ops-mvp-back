package com.ops.ops.modules.notice.application.convenience;

import static com.ops.ops.modules.notice.exception.NoticeExceptionType.NOT_FOUND_NOTICE;

import com.ops.ops.modules.notice.domain.Notice;
import com.ops.ops.modules.notice.domain.dao.NoticeRepository;
import com.ops.ops.modules.notice.exception.NoticeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeConvenience {

    private final NoticeRepository noticeRepository;

    public Notice getValidateExistNotice(final Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(NOT_FOUND_NOTICE));
    }
}
