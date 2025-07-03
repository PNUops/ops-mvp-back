package com.ops.ops.modules.notice.api;

import com.ops.ops.modules.notice.application.NoticeCommandService;
import com.ops.ops.modules.notice.application.NoticeQueryService;
import com.ops.ops.modules.notice.application.dto.request.NoticeRequest;
import com.ops.ops.modules.notice.application.dto.response.NoticeDetailResponse;
import com.ops.ops.modules.notice.application.dto.response.NoticeSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "공지시항 생성", description = "공지사항을 생성합니다. (관리자)")
    @ApiResponse(responseCode = "201", description = "공지사항 생성 성공")
    @PostMapping
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> createNotice(@Valid @RequestBody final NoticeRequest request) {
        noticeCommandService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "공지시항 수정", description = "공지사항을 수정합니다. (관리자)")
    @ApiResponse(responseCode = "204", description = "공지사항 수정 성공")
    @PatchMapping("/{noticeId}")
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> updateNotice(@Valid @RequestBody final NoticeRequest request,
                                             @PathVariable final Long noticeId) {
        noticeCommandService.updateNotice(request, noticeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공지시항 삭제", description = "공지사항을 삭제합니다. (관리자)")
    @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공")
    @DeleteMapping("/{noticeId}")
    @Secured("ROLE_관리자")
    public ResponseEntity<Void> deleteNotice(@PathVariable final Long noticeId) {
        noticeCommandService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공지시항 상세 목록 조회", description = "공지사항 상세 목록을 조회합니다. (공지사항 페이지)")
    @ApiResponse(responseCode = "200", description = "공지사항 상세 목록 조회 성공")
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDetailResponse> getNotice(@PathVariable final Long noticeId) {
        return ResponseEntity.ok(noticeQueryService.getNotice(noticeId));
    }

    @Operation(summary = "공지시항 목록 조회", description = "공지사항 목록을 조회합니다. (메인페이지)")
    @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<NoticeSummaryResponse>> getNotices() {
        return ResponseEntity.ok(noticeQueryService.getNotices());
    }
}
