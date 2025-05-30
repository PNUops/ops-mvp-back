package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamCommandService teamCommandService;

    public TeamDetailResponse getTeamDetail(final Long teamId, final Member member){
        Team team = teamCommandService.validateAndGetTeamById(teamId);

        boolean isLiked = false;
        if (member != null){
            // isLiked = teamLikeRepository...
        }

        return TeamDetailResponse.from(team, isLiked);
    }

}
