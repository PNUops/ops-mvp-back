package com.ops.ops.modules.notice.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지사항 관련 기능")
@RestController
@RequiredArgsConstructor
public class NoticeController {
}
