package com.ops.ops.modules.team.application.convenience;

import static java.util.stream.Collectors.toMap;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamLikeConvenience {

    private final TeamLikeRepository teamLikeRepository;

    public List<TeamSummaryResponse> getAllTeamSummaries(final List<Team> teams, final Member member) {
        Collections.shuffle(teams);

        final Map<Long, Boolean> likeMap =
                (member != null) ? teamLikeRepository.findAllByMemberIdAndTeamIn(member.getId(), teams).stream()
                        .collect(toMap(tl -> tl.getTeam().getId(), TeamLike::getIsLiked))
                        : Collections.emptyMap();

        return teams.stream().map(team -> TeamSummaryResponse.from(team, likeMap.getOrDefault(team.getId(), false)))
                .toList();
    }
}
