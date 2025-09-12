package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamDisplayOrderRequest(
	@NotNull
	Long teamId,
	@NotNull
	Integer displayOrder
) {
}