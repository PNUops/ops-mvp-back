package com.ops.ops.modules.team.application;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import com.ops.ops.modules.team.application.dto.request.PreviewRequest;
import com.ops.ops.modules.team.application.dto.request.ThumbnailRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommandService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;

    public void saveThumbnail(Long teamId, ThumbnailRequest thumbnailRequest) throws IOException {

        verifyImage(thumbnailRequest);

        MultipartFile file = thumbnailRequest.image();
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "defaultName";
        String saveThumbnailName = createSaveThumbnailName(originalFilename);
        Path fullPath = getFullPath(saveThumbnailName);

        verifyTeamExists(teamId);

        file.transferTo(fullPath);
        File image = File.builder()
                .name(originalFilename)
                .filePath(fullPath.toString())
                .teamId(teamId)
                .type(FileImageType.THUMBNAIL)
                .build();
        fileRepository.save(image);
    }

    public void savePreview(Long teamId, PreviewRequest previewRequest) throws IOException {
        validatePreviewImage(previewRequest);
        verifyTeamExists(teamId);

        List<MultipartFile> previewImages = previewRequest.images();
        for (MultipartFile previewImage : previewImages) {
            String originalFilename = previewImage.getOriginalFilename() != null ? previewImage.getOriginalFilename() : "defaultName";
            String savePreviewName = createSaveThumbnailName(originalFilename);
            Path fullPath = getFullPath(savePreviewName);
            previewImage.transferTo(fullPath);

            File image = File.builder()
                    .name(originalFilename)
                    .filePath(fullPath.toString())
                    .teamId(teamId)
                    .type(FileImageType.PREVIEW)
                    .build();
            fileRepository.save(image);
        }
    }

    private void validatePreviewImage(PreviewRequest previewRequest) {
        if (previewRequest.images() == null || previewRequest.images().isEmpty()) {
            throw new FileException(FileExceptionType.NO_IMAGE);
        } else if (previewRequest.images().size() > 6) {
            throw new FileException(FileExceptionType.EXCEED_PREVIEW_LIMIT);
        }
    }

    private void verifyImage(ThumbnailRequest thumbnailRequest) {
        if (thumbnailRequest.image() == null || thumbnailRequest.image().isEmpty()) {
            throw new FileException(FileExceptionType.NO_IMAGE);
        }
    }

    private void verifyTeamExists(Long teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
    }

    private String createSaveThumbnailName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        int i = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(i);
        return uuid + ext;
    }
    private Path getFullPath(String saveThumbnailName) {
        Path uploadDirPath = Paths.get(uploadDir);
        return uploadDirPath.resolve(saveThumbnailName);
    }

	public Team validateAndGetTeamById(final Long teamId) {
		return teamRepository.findById(teamId)
			.orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
	}
}
