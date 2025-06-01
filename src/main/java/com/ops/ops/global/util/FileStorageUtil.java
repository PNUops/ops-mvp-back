package com.ops.ops.global.util;

import com.ops.ops.global.error.FileDeleteFailedException;
import com.ops.ops.global.error.FileNotFoundException;
import com.ops.ops.global.error.FileSaveFailedException;
import com.ops.ops.modules.file.domain.File;
import com.ops.ops.modules.file.domain.FileImageType;
import com.ops.ops.modules.file.domain.dao.FileRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class FileStorageUtil {

    private static final Path ROOT_PATH = Paths.get(System.getProperty("user.dir"));
    private static final Path RESOURCE_PATH = ROOT_PATH.resolve("ops_files");
    private static final Path DEFAULT_FILE_PATH = RESOURCE_PATH.resolve("files");

    private final FileRepository fileRepository;

    static {
        try {
            Files.createDirectories(DEFAULT_FILE_PATH);
        } catch (IOException ignored) {
        }
    }

    public File storeFile(final MultipartFile multipartFile, final Long teamId, final FileImageType type) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileSaveFailedException("업로드할 파일이 비어 있거나 존재하지 않습니다.");
        }

        try {
            final LocalDate today = LocalDate.now();
            final Path uploadDir = DEFAULT_FILE_PATH.resolve(today.toString());
            if (Files.notExists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            final String originalFilename = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            final String randomFilename = UUID.randomUUID() + extension;
            final Path targetFile = uploadDir.resolve(randomFilename);
            multipartFile.transferTo(targetFile.toFile());

            final Path relativePath = RESOURCE_PATH.relativize(targetFile);
            final String filePathForDb = relativePath.toString().replace("\\", "/");

            return fileRepository.save(File.builder()
                    .name(originalFilename)
                    .filePath(filePathForDb)
                    .teamId(teamId)
                    .type(type)
                    .build());

        } catch (IOException e) {
            throw new FileSaveFailedException("로컬 디스크에 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(final Long fileId) {
        final File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("삭제할 파일을 찾을 수 없습니다. ID=" + fileId));

        deletePhysicalFile(fileEntity);
        fileRepository.delete(fileEntity);
    }

    private void deletePhysicalFile(final File fileEntity) {
        final Path fullPath = RESOURCE_PATH.resolve(fileEntity.getFilePath());
        if (Files.exists(fullPath)) {
            try {
                Files.delete(fullPath);
            } catch (IOException | SecurityException e) {
                throw new FileDeleteFailedException("물리 파일 삭제에 실패했습니다. 경로=" + fullPath, e);
            }
        } else {
            throw new FileNotFoundException("삭제하려는 물리 파일이 존재하지 않습니다: " + fullPath);
        }
    }
}
