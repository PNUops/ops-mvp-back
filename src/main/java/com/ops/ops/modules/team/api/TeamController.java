package com.ops.ops.modules.team.api;


import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.dto.request.PreviewDeleteRequest;
import com.ops.ops.modules.team.application.dto.request.ThumbnailRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamCommandService teamCommandService;

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable Long teamId, ThumbnailRequest thumbnailRequest) throws IOException {
        teamCommandService.saveThumbnail(teamId, thumbnailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/teams/{teamId}/image")
    public ResponseEntity<Void> deletePreviewImage(@PathVariable Long teamId, @RequestBody PreviewDeleteRequest previewDeleteRequest) throws IOException {
        teamCommandService.deletePreview(teamId, previewDeleteRequest);
        return ResponseEntity.noContent().build();
    }
}
