package com.ops.ops.modules.file.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public record ThumbnailRequest(
        MultipartFile image
) {
}
