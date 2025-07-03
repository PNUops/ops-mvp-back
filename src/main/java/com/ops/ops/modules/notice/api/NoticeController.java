package com.ops.ops.modules.notice.api;

import com.ops.ops.modules.notice.application.NoticeCommandService;
import com.ops.ops.modules.notice.application.NoticeQueryService;
import com.ops.ops.modules.notice.application.dto.request.NoticeRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지사항 관련 기능")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;

    @PostMapping
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> createNotice(@Valid @RequestBody final NoticeRequest request) {
        noticeCommandService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
