package com.ops.ops.modules.team.application.dto.request;

import java.util.List;

public record PreviewDeleteRequest(
        List<Long> imageIds
) {
}
