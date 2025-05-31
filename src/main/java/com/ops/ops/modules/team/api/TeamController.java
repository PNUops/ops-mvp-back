package com.ops.ops.modules.team.api;


import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.ThumbnailRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;

    @GetMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Resource> getThumbnailImage(@PathVariable Long teamId) throws IOException {
        Pair<Resource, String> result = teamQueryService.findThumbnail(teamId);

        String mimeType = result.b;
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(result.a);
    }

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable Long teamId, ThumbnailRequest thumbnailRequest) throws IOException {

        teamCommandService.saveThumbnail(teamId, thumbnailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
