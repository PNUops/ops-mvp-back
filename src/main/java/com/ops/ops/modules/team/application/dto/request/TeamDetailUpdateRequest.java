package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamDetailUpdateRequest(
        @NotNull(message = "대회 ID는 필수입니다.")
        Long contestId,
        String teamName,
        String projectName,
        String leaderName,
        String overview,
        String productionPath,
        String githubPath,
        String youTubePath,
        String professorName
) {
}
