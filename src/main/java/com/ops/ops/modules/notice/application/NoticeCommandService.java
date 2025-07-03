package com.ops.ops.modules.notice.application;

import com.ops.ops.modules.notice.application.dto.request.NoticeCreateRequest;
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

    public void createNotice(final NoticeCreateRequest request) {
        noticeRepository.save(Notice.builder()
                .title(request.title())
                .description(request.description())
                .build());
    }
}
