package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.contest.exception.ContestExceptionType.ADMIN_ONLY_FOR_PAST_CONTEST;
import static com.ops.ops.modules.contest.exception.ContestExceptionType.CANNOT_CHANGE_CONTEST_FOR_CURRENT;
import static com.ops.ops.modules.contest.exception.ContestExceptionType.CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST;
import static com.ops.ops.modules.contest.exception.ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT;
import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;
import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_관리자;
import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_팀장;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.application.convenience.MemberConvenience;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.convenience.TeamMemberConvenience;
import com.ops.ops.modules.team.application.dto.request.TeamCreateRequest;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.application.dto.response.TeamCreateResponse;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommandService {

    private final FileRepository fileRepository;
    private final FileStorageUtil fileStorageUtil;

    private final TeamRepository teamRepository;

    private final TeamMemberCommandService teamMemberCommandService;
    private final TeamQueryService teamQueryService;

    private final ContestConvenience contestConvenience;
    private final TeamConvenience teamConvenience;
    private final TeamMemberConvenience teamMemberConvenience;
    private final MemberConvenience memberConvenience;

    public void saveThumbnailImage(final Long teamId, final MultipartFile image, final FileImageType thumbnailType) {
        teamConvenience.validateExistTeam(teamId);
        fileRepository.findByTeamIdAndType(teamId, thumbnailType).ifPresent(existingFile -> {
            fileStorageUtil.deleteFile(existingFile.getId());
        });
        fileStorageUtil.storeFile(image, teamId, thumbnailType);
    }

    public void deleteThumbnailImage(Long teamId, FileImageType thumbnailType) {
        teamConvenience.validateExistTeam(teamId);
        fileRepository.findByTeamIdAndType(teamId, thumbnailType).ifPresent(existingFile -> {
            fileStorageUtil.deleteFile(existingFile.getId());
        });
    }

    public void deletePreviewImages(Long teamId, List<Long> ids, FileImageType fileImageType) {
        //todo : 파일 타입 확인 로직 필요
        teamConvenience.validateExistTeam(teamId);
        ids.forEach(fileStorageUtil::deleteFile);
    }

    public void savePreviewImages(Long teamId, List<MultipartFile> images) {
        teamConvenience.validateExistTeam(teamId);
        checkPreviewLimit(teamId, images);
        for (MultipartFile image : images) {
            fileStorageUtil.storeFile(image, teamId, PREVIEW);
        }
    }

    public void deleteTeam(final Long teamId) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        Long leaderMemberId = teamQueryService.getLeaderIdByTeamId(teamId);
        memberConvenience.changeLeaderRoleToRegularMember(leaderMemberId);
        teamMemberConvenience.deleteAllTeamMembersByTeamId(teamId);
        teamRepository.delete(team);
    }

    public void updateTeamDetail(final Long teamId, final Member member, final TeamDetailUpdateRequest request) {
        final Team team = teamConvenience.getValidateExistTeam(teamId);
        final Contest newContest = contestConvenience.getValidateExistContest(request.contestId());
        checkTeamContestChange(team, newContest, member, request.teamName(), request.projectName(),
                request.leaderName());

        updateLeaderIfChanged(team, request.leaderName());

        team.updateDetail(request.leaderName(), request.teamName(), request.projectName(), request.overview(),
                request.productionPath(), request.githubPath(), request.youTubePath(), request.contestId());
    }

    public TeamCreateResponse createTeam(TeamCreateRequest request) {
        final Contest contest = contestConvenience.getValidateExistContest(request.contestId());
        checkIsTeamCreatable(contest);

        final Team team = teamRepository.save(Team.builder()
                .leaderName(request.leaderName())
                .teamName(request.teamName())
                .projectName(request.projectName())
                .overview(request.overview())
                .productionPath(request.productionPath())
                .githubPath(request.githubPath())
                .youTubePath(request.youTubePath())
                .contestId(contest.getId())
                .build());

        teamMemberCommandService.assignFakeTeamMember(team, request.leaderName(), Set.of(ROLE_팀장));

        return TeamCreateResponse.from(team);
    }

    private void checkPreviewLimit(Long teamId, List<MultipartFile> images) {
        long savedCount = fileRepository.countByTeamIdAndType(teamId, PREVIEW);
        if (savedCount + images.size() > 5) {
            throw new FileException(EXCEED_PREVIEW_LIMIT);
        }
    }

    private void checkTeamContestChange(final Team team, final Contest newContest, final Member member,
                                        final String newTeamName, final String newProjectName,
                                        final String newLeaderName) {
        final Contest oldContest = contestConvenience.getValidateExistContest(team.getContestId());
        if (oldContest.getIsCurrent()) {
            checkCurrentContest(team, newContest, newTeamName, newProjectName, newLeaderName);
        } else {
            checkPastContest(newContest, member);
        }
    }

    private void checkCurrentContest(final Team team, final Contest newContest, final String newTeamName,
                                     final String newProjectName, final String newLeaderName) {
        if (team.isContestChanged(newContest.getId())) {
            throw new ContestException(CANNOT_CHANGE_CONTEST_FOR_CURRENT);
        }
        if (team.isTeamInfoChanged(newTeamName, newProjectName, newLeaderName)) {
            throw new ContestException(CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
        }
    }

    private void checkPastContest(final Contest newContest, final Member member) {
        if (!isAdmin(member)) {
            throw new ContestException(ADMIN_ONLY_FOR_PAST_CONTEST);
        }
        if (newContest.getIsCurrent()) {
            throw new ContestException(CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
        }
    }

    private boolean isAdmin(final Member member) {
        return member != null && member.getRoles().contains(ROLE_관리자);
    }

    // 멤버 삭제와 등록 로직이 복잡하여 예외적으로 command service 의존 허용
    private void updateLeaderIfChanged(Team team, String newLeaderName) {
        if (team.isLeaderNameChanged(newLeaderName)) {
            teamMemberCommandService.removeFakeTeamMemberByName(team, team.getLeaderName());
            teamMemberCommandService.assignFakeTeamMember(team, newLeaderName, Set.of(ROLE_팀장));
        }
    }

    private void checkIsTeamCreatable(final Contest contest) {
        if (!contest.isCurrent()) {
            throw new ContestException(CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
        }
    }
}
