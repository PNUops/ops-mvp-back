package com.ops.ops.modules.team.api;


import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;

import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.dto.request.ThumbnailDeleteRequest;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.request.PreviewRequest;
import com.ops.ops.modules.team.application.dto.request.ThumbnailRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable Long teamId, ThumbnailRequest thumbnailRequest) throws IOException {
        teamCommandService.saveThumbnail(teamId, thumbnailRequest);
    @GetMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Resource> getThumbnailImage(@PathVariable Long teamId) throws IOException {
        Pair<Resource, String> result = teamQueryService.findThumbnail(teamId);

        String mimeType = result.b;
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(result.a);
    }

    @PostMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable final Long teamId,
                                                   @RequestPart("image") final MultipartFile image) {
        teamCommandService.saveThumbnailImage(teamId, image, THUMBNAIL);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/teams/{teamId}/image")
    public ResponseEntity<Void> savePreviewImage(@PathVariable Long teamId, PreviewRequest previewRequest) throws IOException {
        teamCommandService.savePreview(teamId, previewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> deleteThumbnailImage(@PathVariable Long teamId,
                                                     @RequestBody ThumbnailDeleteRequest thumbnailDeleteRequest)
            throws IOException {
        teamCommandService.deleteThumbnail(teamId, thumbnailDeleteRequest);
        return ResponseEntity.noContent().build();
    }
}
