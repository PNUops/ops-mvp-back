package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.ContestTeamCommandService;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.domain.dao.ContestTeamRepository;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.TeamMember;
import com.ops.ops.modules.team.domain.dao.TeamMemberRepository;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
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
    private final ContestTeamRepository contestTeamRepository;
    private final ContestRepository contestRepository;
    private final ContestTeamCommandService contestTeamCommandService;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;

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

    public void updateTeamDetail(final Long teamId, final Member member, final TeamDetailUpdateRequest request) {
        final Team team = validateAndGetTeamById(teamId);
        contestTeamCommandService.ValidateAndUpdateContest(
                teamId,
                request.contestId(),
                team,
                member,
                request.teamName(),
                request.projectName(),
                request.leaderName()
        );

        if (team.isLeaderNameChanged(request.leaderName())) {
            // 1. 기존 리더 TeamMember 찾기
            List<TeamMember> teamMembers = team.getTeamMembers();
            TeamMember oldLeader = teamMembers.stream()
                    .filter(tm -> !tm.getIsDeleted())
                    .filter(tm -> memberRepository.findById(tm.getMemberId())
                            .map(Member::getName)
                            .map(name -> name.equals(request.leaderName()))
                            .orElse(false))
                    .findFirst()
                    .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM_MEMBER));

            // 2. 기존 리더 TeamMember 삭제
            teamMemberRepository.delete(oldLeader);

            // 3. 기존 Member가 가짜면 Member도 삭제
            memberRepository.findById(oldLeader.getMemberId()).ifPresent(m -> {
                if (Member.isFake(m)) {
                    memberRepository.delete(m);
                }
            });

            // 4. 가짜 리더 생성 및 저장
            Member fakeLeader = Member.createFake(request.leaderName());
            memberRepository.save(fakeLeader);

            // 5. 새 리더 TeamMember 생성 및 저장
            TeamMember newLeader = team.addTeamMember(fakeLeader.getId());
            teamMemberRepository.save(newLeader);
        }

        team.updateDetail(
                request.teamName(),
                request.projectName(),
                request.leaderName(),
                request.overview(),
                request.productionPath(),
                request.githubPath(),
                request.youTubePath()
        );
    }
}
