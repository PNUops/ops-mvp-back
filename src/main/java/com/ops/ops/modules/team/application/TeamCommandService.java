package com.ops.ops.modules.team.application;

import static com.ops.ops.modules.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import com.ops.ops.global.util.FileStorageUtil;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import com.ops.ops.modules.team.application.dto.request.ThumbnailDeleteRequest;
import com.ops.ops.modules.team.application.dto.request.ThumbnailSaveRequest;
import com.ops.ops.modules.team.domain.Team;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommandService {

    //    @Value("${file.upload-dir}")
//    private String uploadDir;
    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;
    private final FileStorageUtil fileStorageUtil;

//    public void saveThumbnail(Long teamId, ThumbnailSaveRequest thumbnailSaveRequest) throws IOException {
//
//        verifyImage(thumbnailSaveRequest);
//
//        MultipartFile file = thumbnailSaveRequest.image();
//        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "defaultName";
//        String saveThumbnailName = createSaveThumbnailName(originalFilename);
//        Path fullPath = getFullPath(saveThumbnailName);
//
//        validateAndGetTeamById(teamId);
//
//        Files.createDirectories(Paths.get(uploadDir));
//        file.transferTo(fullPath);
//        File image = File.builder()
//                .name(originalFilename)
//                .filePath(fullPath.toString())
//                .teamId(teamId)
//                .type(FileImageType.THUMBNAIL)
//                .build();
//        fileRepository.save(image);
//    }

    public void saveThumbnailImage(final Long teamId, final MultipartFile image, final FileImageType thumbnailType) {
        validateExistTeam(teamId);
        fileRepository.findByTeamIdAndType(teamId, thumbnailType).ifPresent(existingFile -> {
            fileStorageUtil.deleteFile(existingFile.getId());});
        fileStorageUtil.storeFile(image, teamId, thumbnailType);
    }

    public void deleteThumbnail(Long teamId, ThumbnailDeleteRequest thumbnailDeleteRequest) throws IOException {
        validateAndGetTeamById(teamId);

        Long requestImageId = validateThumbnailOwnershipAndGetRequestImageId(teamId, thumbnailDeleteRequest);

        deleteImageFiles(Collections.singletonList(requestImageId));
    }

    private Long validateThumbnailOwnershipAndGetRequestImageId(Long teamId,
                                                                ThumbnailDeleteRequest thumbnailDeleteRequest) {
        Long requestImageId = thumbnailDeleteRequest.imageId();
        File requestThumbnail = fileRepository.findById(requestImageId)
                .orElseThrow(() -> new FileException(FileExceptionType.NOT_EXISTS_THUMBNAIL));
        if (!requestThumbnail.getTeamId().equals(teamId)) {
            throw new FileException(FileExceptionType.REQUEST_NOT_OWN_IMAGE);
        }
        return requestImageId;
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

    private void verifyImage(ThumbnailSaveRequest thumbnailSaveRequest) {
        if (thumbnailSaveRequest.image() == null || thumbnailSaveRequest.image().isEmpty()) {
            throw new FileException(FileExceptionType.NO_IMAGE);
        }
    }

    private String createSaveThumbnailName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        int i = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(i);
        return uuid + ext;
    }

//    private Path getFullPath(String saveThumbnailName) {
//        Path uploadDirPath = Paths.get(uploadDir);
//        return uploadDirPath.resolve(saveThumbnailName);
//    }

    public Team validateAndGetTeamById(final Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }

    private void validateExistTeam(final Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
    }
}