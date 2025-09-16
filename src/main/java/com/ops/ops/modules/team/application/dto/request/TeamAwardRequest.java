package com.ops.ops.modules.team.application.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;

public record TeamAwardRequest(
        String awardName,

        @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "색상 정보가 올바르지 않습니다. (예: #FFFFFF)")
        String awardColor
) {

    @AssertTrue(message = "수상명과 색상 정보가 올바르지 않습니다.")
    public boolean isValidAwardParameters() {
        if (awardName == null && awardColor == null) {
            return true;
        }

        return awardName != null && awardColor != null &&
                !awardName.trim().isEmpty() && !awardColor.trim().isEmpty();
    }
}
