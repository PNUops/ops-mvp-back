package com.ops.ops.modules.team.application;

import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ops.ops.modules.file.domain.File;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final FileRepository fileRepository;
    private final TeamLikeRepository teamLikeRepository;
    private final TeamMemberQueryService teamMemberQueryService;

    public TeamDetailResponse getTeamDetail(final Long teamId, final Member member){
        Team team = teamRepository.findByIdAndIsDeletedFalse(teamId);
        List<String> participants = teamMemberQueryService.getParticipantsbyTeamId(teamId);
        Long leaderId = teamMemberQueryService.getLeaderIdByTeamId(teamId);
        List<Long> previewIds = fileRepository.findAllByTeamIdAndType(teamId, FileImageType.PREVIEW)
                .stream()
                .map(File::getId)
                .toList();

        boolean isLiked = false;
        if (member != null) {
            isLiked = teamLikeRepository.findByMemberIdAndTeam(member.getId(), team)
                    .map(TeamLike::getIsLiked)
                    .orElse(false);
        }
        return TeamDetailResponse.from(team, leaderId,participants, previewIds, isLiked);
    }

}
