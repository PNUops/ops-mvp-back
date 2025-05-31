package com.ops.ops.modules.team.application;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import com.ops.ops.modules.team.application.dto.request.PreviewDeleteRequest;
import com.ops.ops.modules.team.application.dto.request.ThumbnailRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
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
        String fullPath = getFullPath(saveThumbnailName);

        verifyTeamExists(teamId);

        file.transferTo(new java.io.File(fullPath));
        File image = File.builder()
                .name(originalFilename)
                .filePath(fullPath)
                .teamId(teamId)
                .type(FileImageType.THUMBNAIL)
                .build();
        fileRepository.save(image);
    }

    public void deletePreview(Long teamId, PreviewDeleteRequest previewDeleteRequest) throws IOException {
        validateRequest(previewDeleteRequest);
        validateAndGetTeamById(teamId);
        List<Long> requestImageIds = validatePreviewOwnershipAndGetRequestImageId(teamId, previewDeleteRequest);
        deleteImageFiles(requestImageIds);
    }

    private void validateRequest(PreviewDeleteRequest previewDeleteRequest) {
        if (previewDeleteRequest.imageIds() == null || previewDeleteRequest.imageIds().isEmpty()) {
            throw new FileException(FileExceptionType.NOT_INCLUDE_ID);
        }
    }

    private List<Long> validatePreviewOwnershipAndGetRequestImageId(Long teamId, PreviewDeleteRequest previewDeleteRequest) {
        List<Long> imageIds = previewDeleteRequest.imageIds();
        for (Long imageId : imageIds) {
            File requestPreview = fileRepository.findById(imageId)
                    .orElseThrow(() -> new FileException(FileExceptionType.NOT_EXISTS_PREVIEW));
            if (!requestPreview.getTeamId().equals(teamId)) {
                throw new FileException(FileExceptionType.REQUEST_NOT_OWN_IMAGE);
            }
        }
        return imageIds;
    }

    private void deleteImageFiles(List<Long> imageIds) throws IOException {
        for (Long imageId : imageIds) {
            File findFile = fileRepository.findById(imageId)
                    .orElseThrow(() -> new FileException(FileExceptionType.NOT_EXISTS_MATCHING_IMAGE_ID));
            String filePath = findFile.getFilePath();
            Path fullPath = Paths.get(filePath);
            Files.delete(fullPath);
            fileRepository.deleteById(imageId);
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
    private String getFullPath(String saveThumbnailName) {
        return uploadDir + saveThumbnailName;
    }

	public Team validateAndGetTeamById(final Long teamId) {
		return teamRepository.findById(teamId)
			.orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
	}
}
