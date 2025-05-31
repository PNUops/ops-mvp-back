package com.ops.ops.modules.team.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ThumbnailSaveRequest(
        MultipartFile image
) {
}
