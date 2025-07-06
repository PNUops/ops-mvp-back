package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.team.application.convenience.TeamConvenience;
import com.ops.ops.modules.team.application.dto.request.TeamCreateRequest;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
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
    private final TeamRepository teamRepository;
    private final FileStorageUtil fileStorageUtil;
    private final TeamMemberCommandService teamMemberCommandService;
    private final ContestConvenience contestConvenience;
    private final TeamConvenience teamConvenience;

    public void saveThumbnailImage(final Long teamId, final MultipartFile image, final FileImageType thumbnailType) {
        validateExistTeam(teamId);
        fileRepository.findByTeamIdAndType(teamId, thumbnailType).ifPresent(existingFile -> {
            fileStorageUtil.deleteFile(existingFile.getId());
        });
        fileStorageUtil.storeFile(image, teamId, thumbnailType);
    }

    public void deleteThumbnailImage(Long teamId, FileImageType thumbnailType) {
        validateAndGetTeamById(teamId);
        fileRepository.findByTeamIdAndType(teamId, thumbnailType).ifPresent(existingFile -> {
            fileStorageUtil.deleteFile(existingFile.getId());
        });
    }

    public void deletePreviewImages(Long teamId, List<Long> ids, FileImageType fileImageType) {
        validateAndGetTeamById(teamId);
        ids.forEach(fileStorageUtil::deleteFile);
    }

    public void savePreviewImages(Long teamId, List<MultipartFile> images) {
        validateExistTeam(teamId);
        checkPreviewLimit(teamId, images);
        for (MultipartFile image : images) {
            fileStorageUtil.storeFile(image, teamId, PREVIEW);
        }
    }

    private void checkPreviewLimit(Long teamId, List<MultipartFile> images) {
        long savedCount = fileRepository.countByTeamIdAndType(teamId, PREVIEW);
        if (savedCount + images.size() > 5) {
            throw new FileException(EXCEED_PREVIEW_LIMIT);
        }
    }

    private void validateExistTeam(final Long teamId) {
        teamConvenience.getValidateExistTeam(teamId);
    }

    public Team validateAndGetTeamById(final Long teamId) {
        return teamConvenience.getValidateExistTeam(teamId);
    }

    public void deleteTeam(final Long teamId) {
        final Team team = validateAndGetTeamById(teamId);
        teamRepository.delete(team);
    }

    public void updateTeamDetail(final Long teamId, final Member member, final TeamDetailUpdateRequest request) {
        final Team team = validateAndGetTeamById(teamId);

        final Contest newContest = contestConvenience.getValidateExistContest(request.contestId());
        validateTeamContestChange(team, newContest, member, request.teamName(), request.projectName(),
                request.leaderName());

        updateLeaderIfChanged(team, request.leaderName());

        team.updateDetail(request.leaderName(), request.teamName(), request.projectName(), request.overview(),
                request.productionPath(), request.githubPath(), request.youTubePath(), request.contestId());
    }

    public void createTeam(TeamCreateRequest request) {
        final Contest contest = contestConvenience.getValidateExistContest(request.contestId());
        if (!contest.isTeamCreatable()) {
            throw new ContestException(ContestExceptionType.CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
        }

        final Team team = Team.of(request.leaderName(), request.teamName(), request.projectName(), request.overview(),
                request.productionPath(), request.githubPath(), request.youTubePath(), request.contestId());
        teamRepository.save(team);

        teamMemberCommandService.assignFakeTeamMember(team, request.leaderName(), Set.of(MemberRoleType.ROLE_팀장));
    }

    private void validateTeamContestChange(final Team team, final Contest newContest, final Member member,
                                           final String newTeamName, final String newProjectName,
                                           final String newLeaderName) {
        final Contest oldContest = contestConvenience.getValidateExistContest(team.getContestId());
        if (oldContest.getIsCurrent()) {
            validateCurrentContest(team, newContest, newTeamName, newProjectName, newLeaderName);
        } else {
            validatePastContest(newContest, member);
        }
    }

    private void validateCurrentContest(final Team team, final Contest newContest, final String newTeamName,
                                        final String newProjectName, final String newLeaderName) {
        if (team.isContestChanged(newContest.getId())) {
            throw new ContestException(ContestExceptionType.CANNOT_CHANGE_CONTEST_FOR_CURRENT);
        }
        if (team.isTeamInfoChanged(newTeamName, newProjectName, newLeaderName)) {
            throw new ContestException(ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
        }
    }

    private void validatePastContest(final Contest newContest, final Member member) {
        if (!isAdmin(member)) {
            throw new ContestException(ContestExceptionType.ADMIN_ONLY_FOR_PAST_CONTEST);
        }
        if (newContest.getIsCurrent()) {
            throw new ContestException(ContestExceptionType.CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
        }
    }

    private boolean isAdmin(final Member member) {
        return member != null && member.getRoles().contains(MemberRoleType.ROLE_관리자);
    }

    private void updateLeaderIfChanged(Team team, String newLeaderName) {
        if (team.isLeaderNameChanged(newLeaderName)) {
            teamMemberCommandService.removeFakeTeamMemberByName(team, team.getLeaderName());
            teamMemberCommandService.assignFakeTeamMember(team, newLeaderName, Set.of(MemberRoleType.ROLE_팀장));
        }
    }
}
