package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequest(
    @NotNull Long contestId,
    @NotBlank String teamName,
    @NotBlank String projectName,
    @NotBlank String leaderName,
    @NotBlank String overview,
    String productionPath, // nullable
    @NotBlank String githubPath,
    @NotBlank String youTubePath
) {} 