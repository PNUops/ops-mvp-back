package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.team.exception.TeamExceptionType.INVALID_AWARD_PARAMETERS;
import static com.ops.ops.modules.team.exception.TeamExceptionType.INVALID_COLOR_FORMAT;

import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.exception.TeamException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamAdminCommandService {

    private final TeamConvenience teamConvenience;

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[A-Fa-f0-9]{6}$");

    public void updateAward(final Long teamId, final String awardName, final String awardColor) {
        validateAwardParameters(awardName, awardColor);
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        team.updateAward(awardName, awardColor);
    }

    private void validateAwardParameters(final String awardName, final String awardColor) {
        if (awardName == null && awardColor == null) {
            return;
        }

        if (awardName != null && awardColor != null && !awardName.trim().isEmpty() && !awardColor.trim().isEmpty()) {
            validateColorFormat(awardColor);
            return;
        }

        throw new TeamException(INVALID_AWARD_PARAMETERS);
    }

    private void validateColorFormat(final String awardColor) {
        if (!HEX_COLOR_PATTERN.matcher(awardColor.trim()).matches()) {
            throw new TeamException(INVALID_COLOR_FORMAT);
        }
    }
}
