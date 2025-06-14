package com.ops.ops.modules.team.api;


import static com.ops.ops.modules.file.domain.FileImageType.PREVIEW;
import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.request.PreviewDeleteRequest;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSubmissionStatusResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Team Detail", description = "팀 상세보기 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamQueryService teamQueryService;
    private final TeamCommandService teamCommandService;

    @Operation(summary = "팀 전체보기 조회", description = "모든 팀의 전체보기를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 전체보기 조회 성공")
    @GetMapping
    public ResponseEntity<List<TeamSummaryResponse>> getAllTeams(
            @LoginMember final Member member
    ) {
        List<TeamSummaryResponse> responses = teamQueryService.getAllTeamSummaries(member);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "팀 상세보기 작성 여부 조회", description = "팀장인 사용자가 속한 팀의 상세보기 작성 여부를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 상세보기 작성 여부 조회 성공")
    @Secured("ROLE_팀장")
    @GetMapping("/submission-status")
    public ResponseEntity<TeamSubmissionStatusResponse> getTeamSubmissionStatus(
            @LoginMember final Member member
    ) {
        TeamSubmissionStatusResponse responses = teamQueryService.getSubmissionStatus(member);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "팀 상세보기 조회", description = "특정 팀의 상세보기를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 상세보기 조회 성공")
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeamDetail(
            @PathVariable final Long teamId,
            @LoginMember final Member member
    ) {
        TeamDetailResponse response = teamQueryService.getTeamDetail(teamId, member);
        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_팀장")
    @PostMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable final Long teamId,
                                                   @RequestPart("image") final MultipartFile image) {
        teamCommandService.saveThumbnailImage(teamId, image, THUMBNAIL);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{teamId}/image/thumbnail")
    public ResponseEntity<Resource> getThumbnailImage(@PathVariable Long teamId) {
        Pair<Resource, String> result = teamQueryService.findThumbnailImage(teamId);
        String mimeType = result.b;
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(result.a);
    }

    @GetMapping("/{teamId}/image/{imageId}")
    public ResponseEntity<Resource> findPreviewImage(@PathVariable Long teamId, @PathVariable Long imageId) {
        Pair<Resource, String> result = teamQueryService.findPreviewImage(teamId, imageId);
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

    @Operation(summary = "팀 상세보기 수정", description = "특정 팀의 상세보기를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "팀 상세보기 수정 성공")
    @PatchMapping("/{teamId}")
    @Secured("ROLE_팀장")
    public ResponseEntity<Void> updateTeamDetail(
            @PathVariable final Long teamId,
            @Valid @RequestBody final TeamDetailUpdateRequest request,
            @LoginMember final Member member
    ) {
        teamCommandService.updateTeamDetail(teamId, member.getId(), request);
        return ResponseEntity.noContent().build();
    }
}
