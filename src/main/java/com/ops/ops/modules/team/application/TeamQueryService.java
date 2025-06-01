package com.ops.ops.modules.team.application;

import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.member.application.MemberQueryService;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import jakarta.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ops.ops.modules.file.domain.File;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;
    private final TeamLikeRepository teamLikeRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final MemberQueryService memberQueryService;

    public TeamDetailResponse getTeamDetail(final Long teamId, final Member member){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));

        List<String> participants = getParticipantsByTeamId(teamId);
        Long leaderId = getLeaderIdByTeamId(teamId);
        List<Long> previewIds = fileRepository.findAllByTeamIdAndType(teamId, FileImageType.PREVIEW)
                .stream()
                .map(File::getId)
                .toList();

        final boolean isLiked = (member != null)
                ? teamLikeRepository.findByMemberIdAndTeam(member.getId(), team)
                .map(TeamLike::getIsLiked)
                .orElse(false)
                : false;

        return TeamDetailResponse.from(team, leaderId,participants, previewIds, isLiked);
    }

    private Long getLeaderIdByTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);

        List<Long> memberIds = participants.stream()
                .map(TeamMember::getMemberId)
                .toList();
        return getLeaderIdByIds(memberIds);
    }

    private List<String> getParticipantsByTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);
        List<Long> memberIds = participants.stream()
                .map(TeamMember::getMemberId)
                .toList();
        return memberQueryService.getMemberNamesByIds(memberIds);
    }

    private Long getLeaderIdByIds(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .filter(member -> member.getRoles().contains(MemberRoleType.ROLE_팀장))
                .findFirst()
                .map(Member::getId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_LEADER));
    }


    public Pair<Resource,String> findThumbnail(Long teamId) throws IOException {
        File thumbnail = fileRepository.findByTeamIdAndType(teamId, FileImageType.THUMBNAIL)
                .orElseThrow(() -> new FileException(FileExceptionType.NOT_EXISTS_THUMBNAIL));
        String filePath = thumbnail.getFilePath();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileException(FileExceptionType.NOT_EXISTS_THUMBNAIL);
        }

        String mimeType = new MimetypesFileTypeMap().getContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new Pair<>(new ByteArrayResource(fileBytes), mimeType);
    }
    public List<TeamSummaryResponse> getAllTeamSummaries(final Member member) {
        List<Team> teams = teamRepository.findAll();
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
