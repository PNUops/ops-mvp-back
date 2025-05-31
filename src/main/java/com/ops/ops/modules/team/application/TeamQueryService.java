package com.ops.ops.modules.team.application;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final FileRepository fileRepository;

    public Resource findPreview(Long teamId, Long imageId) throws IOException {
        File previewFile = fileRepository.findById(imageId)
                .orElseThrow(() -> new FileException(FileExceptionType.NOT_EXISTS_PREVIEW));

        validateOwnership(teamId, previewFile);

        String filePath = previewFile.getFilePath();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileException(FileExceptionType.NOT_EXISTS_PREVIEW);
        }

        byte[] fileBytes = Files.readAllBytes(path);
        return new ByteArrayResource(fileBytes);
    }

    private void validateOwnership(Long teamId, File previewFile) {
        if (!previewFile.getTeamId().equals(teamId)) {
            throw new FileException(FileExceptionType.REQUEST_NOT_OWN_IMAGE);
        }
    }
}
