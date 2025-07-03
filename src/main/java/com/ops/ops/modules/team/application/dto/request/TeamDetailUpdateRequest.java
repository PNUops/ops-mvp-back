package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamDetailUpdateRequest(
        @NotNull(message = "대회 ID는 필수입니다.") Long contestId,
        @NotBlank(message = "팀명은 필수입니다.") String teamName,
        @NotBlank(message = "프로젝트명은 필수입니다.") String projectName,
        @NotBlank(message = "팀장 이름은 필수입니다.") String leaderName,
        @NotBlank(message = "프로젝트 설명은 필수입니다.") String overview,
        String productionPath,
        @NotBlank(message = "깃헙 주소는 필수입니다.") String githubPath,
        @NotBlank(message = "유튜브 주소는 필수입니다.") String youTubePath
) {}