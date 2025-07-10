package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;
import static com.ops.ops.modules.file.exception.FileExceptionType.NOT_EXISTS_PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.NOT_EXISTS_THUMBNAIL;
import static com.ops.ops.modules.file.exception.FileExceptionType.NOT_WEBP_CONVERTED;
import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_팀장;
import static com.ops.ops.modules.member.exception.MemberExceptionType.NOT_FOUND_LEADER;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static com.ops.ops.modules.team.exception.TeamMemberExceptionType.NOT_FOUND_TEAM_MEMBER;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.convenience.TeamLikeConvenience;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.application.dto.response.TeamMemberResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamLike;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamLikeRepository;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamMemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {

    private final FileRepository fileRepository;
    private final FileStorageUtil fileStorageUtil;

    private final TeamRepository teamRepository;
    private final TeamLikeRepository teamLikeRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final ContestConvenience contestConvenience;
    private final MemberConvenience memberConvenience;
    private final TeamConvenience teamConvenience;
    private final TeamLikeConvenience teamLikeConvenience;

    public TeamDetailResponse getTeamDetail(final Long teamId, final Member member) {
        final Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));

        final Contest contest = contestConvenience.getValidateExistContest(team.getContestId());

        final List<TeamMemberResponse> teamMembers = getTeamMembersByTeamId(teamId);
        final Long leaderId = getLeaderIdByTeamId(teamId);

        final List<Long> previewIds = fileRepository.findAllByTeamIdAndType(teamId, FileImageType.PREVIEW)
                .stream()
                .map(File::getId)
                .toList();

        final boolean isLiked = (member != null) ? teamLikeRepository.findByMemberIdAndTeam(member.getId(), team)
                .map(TeamLike::getIsLiked).orElse(false) : false;

        return TeamDetailResponse.from(contest, team, leaderId, teamMembers, previewIds, isLiked);
    }

    public Pair<Resource, String> findThumbnailImage(final Long teamId) {
        teamConvenience.validateExistTeam(teamId);
        final File findFile = fileRepository.findByTeamIdAndType(teamId, THUMBNAIL)
                .orElseThrow(() -> new FileException(NOT_EXISTS_THUMBNAIL));
        checkImageConverted(findFile);
        return fileStorageUtil.findFileAndType(findFile.getId());
    }

    public Pair<Resource, String> findPreviewImage(Long teamId, Long imageId) {
        teamConvenience.validateExistTeam(teamId);
        final File findFile = fileRepository.findById(imageId).orElseThrow(() -> new FileException(NOT_EXISTS_PREVIEW));
        checkImageConverted(findFile);
        return fileStorageUtil.findFileAndType(findFile.getId());
    }

    public TeamSubmissionStatusResponse getSubmissionStatus(final Member member) {
        TeamMember teamMember = teamMemberRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new TeamMemberException(NOT_FOUND_TEAM_MEMBER));
        Team team = teamMember.getTeam();

        return TeamSubmissionStatusResponse.fromEntity(team);
    }

    private void checkImageConverted(File findFile) {
        if (!findFile.isWebpConverted()) {
            throw new FileException(NOT_WEBP_CONVERTED);
        }
    }

    public Long getLeaderIdByTeamId(final Long teamId) {
        List<TeamMember> participants = teamMemberRepository.findAllByTeamId(teamId);

        List<Long> memberIds = participants.stream()
                .map(TeamMember::getMemberId)
                .toList();
        return getLeaderIdByIds(memberIds);
    }

    private List<TeamMemberResponse> getTeamMembersByTeamId(final Long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamId(teamId);
        List<Long> memberIds = teamMembers.stream()
                .map(TeamMember::getMemberId)
                .toList();
        if (memberIds.isEmpty()) {
            return List.of();
        }
        return memberConvenience.findAllById(memberIds)
                .stream()
                .filter(member -> !member.isTeamLeader())
                .map(member -> new TeamMemberResponse(member.getId(), member.getName()))
                .toList();
    }

    private Long getLeaderIdByIds(List<Long> memberIds) {
        return memberConvenience.findAllById(memberIds)
                .stream()
                .filter(member -> member.getRoles().contains(ROLE_팀장))
                .findFirst()
                .map(Member::getId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_LEADER));
    }

}
