package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamLikeToggleRequest(
	@NotNull Boolean isLiked
) {}
