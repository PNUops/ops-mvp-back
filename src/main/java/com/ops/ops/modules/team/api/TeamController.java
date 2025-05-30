package com.ops.ops.modules.team.api;


import com.ops.ops.modules.file.application.FileCommandService;
import com.ops.ops.modules.file.application.dto.ThumbnailRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final FileCommandService fileCommandService;

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable("teamId") Long teamId, ThumbnailRequest thumbnailRequest)
            throws IOException {

        fileCommandService.saveThumbnail(teamId, thumbnailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
