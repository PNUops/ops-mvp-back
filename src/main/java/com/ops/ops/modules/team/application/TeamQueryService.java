package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final TeamMemberQueryService teamMemberQueryService;

    public TeamDetailResponse getTeamDetail(final Long teamId, final Member member){
        Team team = teamRepository.findByIdAndIsDeletedFalse(teamId);
        List<String> participants = teamMemberQueryService.getParticipantsbyTeamId(teamId);
        Long leaderId = teamMemberQueryService.getLeaderIdByTeamId(teamId);

        boolean isLiked = false;

        if (member != null){
            //isLiked = TeamLikeRepository.findByMemberIdAndTeam(member.getId(), teamdId)
        }

        return TeamDetailResponse.from(team, leaderId, participants, isLiked);
    }

}
