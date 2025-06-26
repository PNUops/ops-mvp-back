package com.ops.ops.modules.team.application;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_TEAM_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommandService {

    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;
    private final FileStorageUtil fileStorageUtil;

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

    public void savePreviewImages(Long teamId, List<MultipartFile> images, FileImageType fileImageType) {
        validateExistTeam(teamId);
        for (MultipartFile image : images) {
            fileStorageUtil.storeFile(image, teamId, fileImageType);
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
        checkTeamLeader(member, team);
        team.updateDetail(request.overview(), request.githubPath(), request.youTubePath());
    }

    private void checkTeamLeader(final Member member, final Team team) {
        if (!team.isTeamLeader(member)) {
            throw new TeamException(CANNOT_MATCH_TEAM_LEADER_NAME);
        }
    }
}
