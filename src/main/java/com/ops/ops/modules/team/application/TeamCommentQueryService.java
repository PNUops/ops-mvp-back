package com.ops.ops.modules.team.application;

import static java.util.stream.Collectors.toMap;

import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.dto.response.TeamCommentResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamComment;
import com.ops.ops.modules.team.domain.dao.TeamCommentRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamCommentQueryService {

    private final TeamCommentRepository teamCommentRepository;

    private final MemberConvenience memberConvenience;
    private final TeamConvenience teamConvenience;

    public List<TeamCommentResponse> getComments(final Long teamId) {
        Team team = teamConvenience.getValidateExistTeam(teamId);
        List<TeamComment> comments = teamCommentRepository.findAllByTeamIdOrderByIdDesc(team.getId());

        List<Long> memberIds = comments.stream()
                .map(TeamComment::getMemberId)
                .distinct()
                .toList();

        Map<Long, String> memberIdNameMap = memberConvenience.findAllById(memberIds)
                .stream()
                .collect(toMap(Member::getId, Member::getName));

        return comments.stream()
                .map(comment -> new TeamCommentResponse(
                        comment.getId(),
                        comment.getDescription(),
                        comment.getMemberId(),
                        memberIdNameMap.get(comment.getMemberId()),
                        team.getId()
                ))
                .toList();
    }
}
