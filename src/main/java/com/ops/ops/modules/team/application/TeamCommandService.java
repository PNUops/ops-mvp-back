package com.ops.ops.modules.team.application;

import com.ops.ops.modules.team.application.dto.ThumbnailRequest;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import com.ops.ops.modules.team.domain.dao.TeamRepository;
import com.ops.ops.modules.team.exception.TeamException;
import com.ops.ops.modules.team.exception.TeamExceptionType;
import java.io.IOException;
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
}
