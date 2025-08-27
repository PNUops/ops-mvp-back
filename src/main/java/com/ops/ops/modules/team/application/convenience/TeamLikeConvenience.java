package com.ops.ops.modules.team.application.convenience;

import static com.ops.ops.modules.team.domain.SortType.RANDOM;
import static java.util.stream.Collectors.toMap;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.SortType;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamLikeConvenience {

    private final TeamLikeRepository teamLikeRepository;

    public List<TeamSummaryResponse> getAllTeamSummaries(final List<Team> teams, final Member member,
                                                         final SortType mode) {
        if (mode.equals(RANDOM)) {
            if (member != null) {
                Random seed = new Random(member.getId());
                Collections.shuffle(teams, seed);
            } else {
                Collections.shuffle(teams);
            }
        }

        final Map<Long, Boolean> likeMap =
                (member != null) ? teamLikeRepository.findAllByMemberIdAndTeamIn(member.getId(), teams).stream()
                        .collect(toMap(tl -> tl.getTeam().getId(), TeamLike::getIsLiked))
                        : Collections.emptyMap();

        return teams.stream().map(team -> TeamSummaryResponse.from(team, likeMap.getOrDefault(team.getId(), false)))
                .toList();
    }

    public void deleteAllByTeamId(final Long teamId) {
        teamLikeRepository.deleteAllByTeamId(teamId);
    }
}
