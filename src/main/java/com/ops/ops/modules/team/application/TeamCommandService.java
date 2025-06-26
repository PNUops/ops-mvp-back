package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.exception.FileExceptionType.EXCEED_PREVIEW_LIMIT;
import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.domain.Team;
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
	public void updateTeamDetail(final Long teamId, final Long memberId, final TeamDetailUpdateRequest request) {
		final Team team = validateAndGetTeamById(teamId);
		team.updateDetail(request.overview(), request.githubPath(), request.youTubePath());
	}
}
