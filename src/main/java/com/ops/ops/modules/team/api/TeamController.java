package com.ops.ops.modules.team.api;


import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;

import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.dto.request.ThumbnailDeleteRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable final Long teamId,
                                                   @RequestPart("image") final MultipartFile image) {
        teamCommandService.saveThumbnailImage(teamId, image, THUMBNAIL);
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
