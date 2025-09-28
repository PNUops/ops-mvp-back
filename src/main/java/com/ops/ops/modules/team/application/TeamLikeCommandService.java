package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.team.exception.TeamLikeExceptionType.ALREADY_LIKED;
import static com.ops.ops.modules.team.exception.TeamLikeExceptionType.ALREADY_UNLIKED;
import static com.ops.ops.modules.team.exception.TeamLikeExceptionType.LIKE_LIMIT_EXCEEDED;

import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.dto.response.TeamLikeToggleResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.exception.TeamLikeException;
import com.ops.ops.modules.team.exception.TeamLikeExceptionType;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamLikeCommandService {
    private static final int MAX_LIKES_PER_CONTEST = 2;

    private final TeamLikeRepository teamLikeRepository;
    private final TeamConvenience teamConvenience;
    private final ContestConvenience contestConvenience;

    public TeamLikeToggleResponse toggleLike(Long memberId, Long teamId, Boolean isLiked) {
        Team team = teamConvenience.getValidateExistTeam(teamId);
        contestConvenience.checkVotePeriodNow(team.getContestId(),
                ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());

        Optional<TeamLike> teamLikeOptional = teamLikeRepository.findByMemberIdAndTeam(memberId, team);

        return teamLikeOptional.map(teamLike -> handleExistingLike(teamLike, isLiked, memberId, team.getContestId()))
                .orElseGet(() -> handleFirstTimeLike(memberId, team, isLiked));
    }

    private TeamLikeToggleResponse handleFirstTimeLike(Long memberId, Team team, Boolean isLiked) {
        long currentLikeCount = countCurrentMemberLikes(memberId, team.getContestId());

        if (isLiked) {
            validateLikeLimit(currentLikeCount);
            currentLikeCount++;
        }

        saveTeamLike(memberId, team, isLiked);

        String message = isLiked ? "좋아요가 처음 등록되었습니다." : "좋아요가 비활성화된 상태로 초기화되었습니다.";
        return new TeamLikeToggleResponse(team.getId(), isLiked, message, currentLikeCount);
    }

    private TeamLikeToggleResponse handleExistingLike(TeamLike teamLike,
                                                      Boolean isLiked,
                                                      Long memberId,
                                                      Long contestId) {
        if (Objects.equals(teamLike.getIsLiked(), isLiked)) { // 좋아요 상태 변화가 없는 경우
            TeamLikeExceptionType exceptionType = isLiked ? ALREADY_LIKED : ALREADY_UNLIKED;
            throw new TeamLikeException(exceptionType);
        }

        long currentLikeCount = countCurrentMemberLikes(memberId, contestId);

        // 좋아요 상태 변경 처리
        if (isLiked) { // 좋아요 등록
            validateLikeLimit(currentLikeCount);
            currentLikeCount++;
        } else { // 좋아요 취소
            currentLikeCount--;
        }

        teamLike.setLiked(isLiked);

        String message = isLiked ? "좋아요가 등록되었습니다." : "좋아요가 취소되었습니다.";
        return new TeamLikeToggleResponse(teamLike.getTeam().getId(), isLiked, message, currentLikeCount);
    }

    public long countCurrentMemberLikes(Long memberId, Long contestId) {
        contestConvenience.getValidateExistContest(contestId);
        return teamLikeRepository.countMemberLikesInContest(memberId, contestId);
    }

    private void validateLikeLimit(long currentLikeCount) {
        if (currentLikeCount >= MAX_LIKES_PER_CONTEST) {
            throw new TeamLikeException(LIKE_LIMIT_EXCEEDED);
        }
    }

    private void saveTeamLike(Long memberId, Team team, Boolean isLiked) {
        teamLikeRepository.save(TeamLike.builder()
                .memberId(memberId)
                .team(team)
                .isLiked(isLiked)
                .build());
    }
}
