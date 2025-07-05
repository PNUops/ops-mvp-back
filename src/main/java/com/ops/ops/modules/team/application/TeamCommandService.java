package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.convenience.ContestConvenience;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.application.MemberCommandService;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.application.dto.request.TeamCreateRequest;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import java.util.List;
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
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ContestConvenience contestConvenience;
    private final MemberCommandService memberCommandService;

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
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    public Team validateAndGetTeamById(final Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
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

        assignFakeLeader(request.leaderName(), team);
    }

    private void validateTeamContestChange(final Team team, final Contest newContest, final Member member,
                                           final String newTeamName, final String newProjectName,
                                           final String newLeaderName
    ) {
        final Contest OldContest = contestConvenience.getValidateExistContest(team.getContestId());

        if (OldContest.getIsCurrent()) {
            if (team.isContestChanged(newContest.getId())) {
                throw new ContestException(ContestExceptionType.CANNOT_CHANGE_CONTEST_FOR_CURRENT);
            }
            if (team.isTeamInfoChanged(newTeamName, newProjectName, newLeaderName)) {
                throw new ContestException(ContestExceptionType.CANNOT_UPDATE_TEAM_INFO_FOR_CURRENT);
            }
        } else {
            boolean isAdmin = member != null && member.getRoles().contains(MemberRoleType.ROLE_관리자);
            if (!isAdmin) {
                throw new ContestException(ContestExceptionType.ADMIN_ONLY_FOR_PAST_CONTEST);
            }
            if (newContest.getIsCurrent()) {
                throw new ContestException(ContestExceptionType.CANNOT_CREATE_TEAM_OF_CURRENT_CONTEST);
            }
        }
    }

    private void updateLeaderIfChanged(Team team, String newLeaderName) {
        if (team.isLeaderNameChanged(newLeaderName)) {
            removeCurrentLeader(team);
            assignFakeLeader(newLeaderName, team);
        }
    }

    private void removeCurrentLeader(final Team team) {
        final TeamMember oldLeader = team.findTeamMemberByName(team.getLeaderName(), memberRepository);
        teamMemberRepository.delete(oldLeader);
        memberRepository.findById(oldLeader.getMemberId())
                .filter(Member::isFake)
                .ifPresent(memberRepository::delete);
    }

    private void assignFakeLeader(final String leaderName, final Team team) {
        final Member fakeLeader = memberCommandService.createFakeMember(leaderName);
        memberRepository.saveAndFlush(fakeLeader);
        final TeamMember teamLeader = team.addTeamMember(fakeLeader.getId());
        teamMemberRepository.save(teamLeader);
    }
}
