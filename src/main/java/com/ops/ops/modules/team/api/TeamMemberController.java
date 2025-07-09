package com.ops.ops.modules.team.api;

import static org.springframework.http.HttpStatus.CREATED;

import com.ops.ops.modules.team.application.TeamMemberCommandService;
import com.ops.ops.modules.team.application.dto.request.TeamMemberCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "팀원", description = "팀원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams/{teamId}/members")
@Secured("ROLE_관리자")
public class TeamMemberController {

    private final TeamMemberCommandService teamMemberCommandService;

    @Operation(summary = "팀원 등록", description = "특정 팀에 팀원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "팀원 등록 성공")
    @PostMapping
    public ResponseEntity<Void> createTeamMember(
            @PathVariable final Long teamId,
            @Valid @RequestBody final TeamMemberCreateRequest request
    ) {
        teamMemberCommandService.createTeamMember(teamId, request.teamMemberName());
        return ResponseEntity.status(CREATED).build();
    }

    @Operation(summary = "팀원 삭제", description = "특정 팀의 팀원을 삭제합니다. (소프트 삭제)")
    @ApiResponse(responseCode = "204", description = "팀원 삭제 성공")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteTeamMember(
            @Parameter(description = "팀 ID") @PathVariable final Long teamId,
            @Parameter(description = "멤버 ID") @PathVariable final Long memberId
    ) {
        teamMemberCommandService.deleteTeamMember(teamId, memberId);
        return ResponseEntity.noContent().build();
    }
}
