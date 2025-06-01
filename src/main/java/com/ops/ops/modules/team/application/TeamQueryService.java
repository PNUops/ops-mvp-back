package com.ops.ops.modules.team.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final TeamMemberQueryService teamMemberQueryService;
    private final TeamLikeRepository teamLikeRepository;


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

    public List<TeamSummaryResponse> getAllTeamSummaries(final Member member) {
        List<Team> teams = teamRepository.findAllByIsDeletedFalse();
        Set<Long> likedTeamIds = (member != null)
                ? teamLikeRepository.findByMemberIdAndTeamIn(member.getId(), teams).stream()
                .map(teamLike -> teamLike.getTeam().getId())
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return teams.stream()
                .map(team -> TeamSummaryResponse.from(team, likedTeamIds.contains(team.getId())))
                .toList();
    }

}
