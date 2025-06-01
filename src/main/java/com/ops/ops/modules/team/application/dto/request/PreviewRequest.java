package com.ops.ops.modules.team.application.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PreviewRequest(
        List<MultipartFile> images
) {
}
