package com.ops.ops.modules.file.api;

import com.ops.ops.modules.file.application.FileService;
import com.ops.ops.modules.file.application.dto.ThumbnailRequest;
import com.ops.ops.modules.file.exception.FileException;
import com.ops.ops.modules.file.exception.FileExceptionType;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity saveThumbnailImage(@PathVariable("teamId") Long teamId, ThumbnailRequest thumbnailRequest)
            throws IOException {
        if (thumbnailRequest.getImage() == null || thumbnailRequest.getImage().isEmpty()) {
            throw new FileException(FileExceptionType.NO_IMAGE);
        }
        fileService.saveThumbnail(teamId, thumbnailRequest.getImage());
        return ResponseEntity.noContent().build();
    }
}
