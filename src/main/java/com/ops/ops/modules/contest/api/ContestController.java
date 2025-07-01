package com.ops.ops.modules.contest.api;

import com.ops.ops.modules.contest.application.ContestQueryService;
import com.ops.ops.modules.contest.application.dto.ContestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contests")
public class ContestController {
    private final ContestQueryService contestQueryService;

    @Operation(summary = "모든 대회 조회", description = "등록된 모든 대회를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 대회 조회 성공")
    @GetMapping
    public ResponseEntity<List<ContestResponse>> getAllContests() {
        List<ContestResponse> responses = contestQueryService.getAllContests();
        return ResponseEntity.ok(responses);
    }
}
