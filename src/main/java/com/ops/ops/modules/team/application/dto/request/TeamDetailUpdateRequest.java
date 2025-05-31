package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamDetailUpdateRequest(
        @NotBlank(message = "수정할 개요 내용은 비어 있을 수 없습니다.") String overview,
        @NotBlank(message = "수정할 깃헙 주소 내용은 비어 있을 수 없습니다.") String githubPath,
        @NotBlank(message = "수정할 유튜브 주소 내용은 비어 있을 수 없습니다.") String youTubePath
) {}