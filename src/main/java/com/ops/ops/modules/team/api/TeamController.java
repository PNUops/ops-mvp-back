package com.ops.ops.modules.team.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.team.application.TeamQueryService;
import com.ops.ops.modules.team.application.dto.response.TeamDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

import com.ops.ops.modules.team.application.TeamCommandService;
import com.ops.ops.modules.team.application.dto.ThumbnailRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Team Detail", description = "팀 상세보기 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamQueryService teamQueryService;

    // 팀 상세보기 조회
    @Operation(summary = "팀 상세보기 조회", description = "특정 팀의 상세보기를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "팀 상세보기 조회 성공")
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeamDetail(
            @PathVariable("teamId") final Long teamId,
            @LoginMember final Member member
    ) {
        TeamDetailResponse response = teamQueryService.getTeamDetail(teamId, member);
        return ResponseEntity.ok(response);
    }

    private final TeamCommandService teamCommandService;

    @PostMapping("/teams/{teamId}/image/thumbnail")
    public ResponseEntity<Void> saveThumbnailImage(@PathVariable Long teamId, ThumbnailRequest thumbnailRequest) throws IOException {

        teamCommandService.saveThumbnail(teamId, thumbnailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
