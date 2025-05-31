package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamCommandService teamCommandService;
    private final TeamMemberQueryService teamMemberQueryService;

    public TeamDetailResponse getTeamDetail(final Long teamId/*, final Member member*/){
        Team team = teamCommandService.validateAndGetTeamById(teamId);
        List<String> participants = teamMemberQueryService.getParticipantsbyTeamId(teamId);
        Long leaderId = teamMemberQueryService.getLeaderIdByTeamId(teamId);

        boolean isLiked = false;
        /*
        if (member != null){
            // isLiked = teamLikeRepository...
        }
        */

        return TeamDetailResponse.from(team, leaderId, participants, isLiked);
    }

}
