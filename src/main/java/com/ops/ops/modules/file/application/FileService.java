package com.ops.ops.modules.file.application;

import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileRepository fileRepository;

    public void saveThumbnail(Long teamId, MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "defaultName";

        String saveThumbnailName = createSaveThumbnailName(originalFilename);

        String fullPath = getFullPath(saveThumbnailName);
        file.transferTo(new java.io.File(fullPath));
        File image = File.builder()
                .name(originalFilename)
                .filePath(fullPath)
                .teamId(teamId)
                .type(FileImageType.THUMBNAIL)
                .build();
        fileRepository.save(image);
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
