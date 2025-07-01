package com.ops.ops.modules.contest.api;

import com.ops.ops.global.security.annotation.LoginMember;
import com.ops.ops.modules.contest.application.ContestCommandService;
import com.ops.ops.modules.contest.application.ContestQueryService;
import com.ops.ops.modules.contest.application.dto.ContestCreateRequest;
import com.ops.ops.modules.contest.application.dto.ContestResponse;
import com.ops.ops.modules.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contests")
public class ContestController {
    private final ContestQueryService contestQueryService;
    private final ContestCommandService contestCommandService;

    @Operation(summary = "모든 대회 조회", description = "등록된 모든 대회를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 대회 조회 성공")
    @GetMapping
    public ResponseEntity<List<ContestResponse>> getAllContests() {
        List<ContestResponse> responses = contestQueryService.getAllContests();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "대회 등록", description = "새로운 대회를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "대회 등록 성공")
    @PostMapping
    public ResponseEntity<Void> createContest(
            @Valid @RequestBody final ContestCreateRequest request,
            @LoginMember final Member member
    ) {
        contestCommandService.createContest(request.contestName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
