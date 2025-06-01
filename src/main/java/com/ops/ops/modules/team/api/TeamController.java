package com.ops.ops.modules.team.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.request.TeamDetailUpdateRequest;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import com.ops.ops.modules.team.application.dto.response.TeamSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

import static com.ops.ops.modules.file.domain.FileImageType.THUMBNAIL;

import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.dto.request.ThumbnailDeleteRequest;
import java.io.IOException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Team Detail", description = "팀 상세보기 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamQueryService teamQueryService;

    @Operation(summary = "팀 전체보기 조회", description = "모든 팀의 전체보기를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 전체보기 조회 성공")
    @GetMapping
    public ResponseEntity<List<TeamSummaryResponse>> getAllTeams(
            @LoginMember final Member member
    ) {
        List<TeamSummaryResponse> responses = teamQueryService.getAllTeamSummaries(member);
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

    @Operation(summary = "팀 상세보기 수정", description = "특정 팀의 상세보기를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "팀 상세보기 수정 성공")
    @PatchMapping("/{teamId}")
    public ResponseEntity<Void> updateTeamDetail(
            @PathVariable final Long teamId,
            @Valid @RequestBody final TeamDetailUpdateRequest request,
            @LoginMember final Member member
    ) {
        teamCommandService.updateTeamDetail(teamId, member.getId(), request);
        return ResponseEntity.noContent().build();
    }
}
