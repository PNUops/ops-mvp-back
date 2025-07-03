package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.contest.application.ContestCommandService;
import com.ops.ops.modules.contest.domain.Contest;
import com.ops.ops.modules.contest.domain.dao.ContestRepository;
import com.ops.ops.modules.contest.exception.ContestException;
import com.ops.ops.modules.contest.exception.ContestExceptionType;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.member.domain.Member;
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
    private final ContestCommandService contestCommandService;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ContestRepository contestRepository;

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

    private void changeToFakeLeader(final Team team, final String newLeaderName) {
        // 1. 기존 리더 TeamMember 찾기
        TeamMember oldLeader = team.findTeamMemberByName(team.getLeaderName(), memberRepository);

        // 2. 기존 리더 TeamMember 삭제
        teamMemberRepository.delete(oldLeader);

        // 3. 기존 Member가 가짜면 Member도 삭제
        memberRepository.findById(oldLeader.getMemberId())
                .filter(Member::isFake)
                .ifPresent(memberRepository::delete);

        // 4. 가짜 리더 생성 및 저장
        Member fakeLeader = memberRepository.save(Member.createFake(newLeaderName));

        // 5. 팀 리더 변경 및 TeamMember 생성
        TeamMember newLeader = team.changeLeaderTo(fakeLeader, team.getLeaderName());

        // 6. 새 TeamMember 저장
        teamMemberRepository.save(newLeader);
    }

    public void updateTeamDetail(final Long teamId, final Member member, final TeamDetailUpdateRequest request) {
        final Team team = validateAndGetTeamById(teamId);

        Contest newContest = contestRepository.findById(request.contestId())
                .orElseThrow(() -> new ContestException(ContestExceptionType.NOT_FOUND_CONTEST));
        team.changeContest(newContest, member, request.teamName(), request.projectName(), request.leaderName());

        if (team.isLeaderNameChanged(request.leaderName())) {
            changeToFakeLeader(team, request.leaderName());
        }

        team.updateDetail(request.teamName(), request.leaderName(), request.overview(),
                request.productionPath(), request.githubPath(), request.youTubePath());
    }

    public void deleteTeam(final Long teamId) {
        final Team team = validateAndGetTeamById(teamId);
        teamRepository.delete(team);
    }

    public void createTeam(TeamCreateRequest request, Member member) {
        final Contest contest = contestCommandService.validateAndGetContestById(request.contestId());
        contest.validateTeamCreatable();

        final Team team = Team.of(request.leaderName(), request.teamName(), request.projectName(), request.overview(),
                request.productionPath(), request.githubPath(), request.youTubePath(), contest
        );
        teamRepository.save(team);

        final Member leader = memberRepository.saveAndFlush(Member.createFake(request.leaderName()));
        final TeamMember teamLeader = team.addTeamMember(leader.getId());
        teamMemberRepository.save(teamLeader);
    }
}
