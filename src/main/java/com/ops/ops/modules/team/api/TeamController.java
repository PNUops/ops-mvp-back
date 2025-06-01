package com.ops.ops.modules.team.api;


import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;

import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.request.PreviewDeleteRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;

    @Secured("ROLE_팀장")
    @PostMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable final Long teamId,
                                                   @RequestPart("image") final MultipartFile image) {
        teamCommandService.saveThumbnailImage(teamId, image, THUMBNAIL);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured("ROLE_비회원")
    @GetMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Resource> getThumbnailImage(@PathVariable Long teamId) {
        Pair<Resource, String> result = teamQueryService.findThumbnailImage(teamId);
        String mimeType = result.b;
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(result.a);
    }

    @Secured("ROLE_팀장")
    @DeleteMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Void> deleteThumbnailImage(@PathVariable Long teamId) {
        teamCommandService.deleteThumbnailImage(teamId, THUMBNAIL);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_팀장")
    @PostMapping("/{teamId}/image")
    public ResponseEntity<Void> savePreviewImage(@PathVariable Long teamId,
                                                 @RequestPart("images") final List<MultipartFile> images) {
        teamCommandService.savePreviewImages(teamId, images, PREVIEW);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured("ROLE_팀장")
    @DeleteMapping("/{teamId}/image")
    public ResponseEntity<Void> deletePreviewImage(@PathVariable Long teamId,
                                                   @RequestBody PreviewDeleteRequest previewDeleteRequest) {
        teamCommandService.deletePreviewImages(teamId, previewDeleteRequest.imageIds(), PREVIEW);
        return ResponseEntity.noContent().build();
    }
}
