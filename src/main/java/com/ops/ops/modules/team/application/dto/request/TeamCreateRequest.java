package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequest(
        @NotNull(message = "대회 ID는 필수입니다.")
        Long contestId,
        @NotBlank(message = "팀명은 필수입니다.")
        String teamName,
        @NotBlank(message = "프로젝트명은 필수입니다.")
        String projectName,
        @NotBlank(message = "팀장명은 필수입니다.")
        String leaderName,
        @NotBlank(message = "개요는 필수입니다.")
        String overview,
        String productionPath, // nullable
        @NotBlank(message = "깃헙주소는 필수입니다.")
        String githubPath,
        @NotBlank(message = "유튜브주소는 필수입니다.")
        String youTubePath
) {
}
