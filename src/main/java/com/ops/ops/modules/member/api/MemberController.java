package com.ops.ops.modules.member.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.ops.ops.modules.member.application.MemberCommandService;
import com.ops.ops.modules.member.application.MemberQueryService;
import com.ops.ops.modules.member.application.dto.request.EmailAuthConfirmRequest;
import com.ops.ops.modules.member.application.dto.request.EmailAuthRequest;
import com.ops.ops.modules.member.application.dto.request.PasswordUpdateRequest;
import com.ops.ops.modules.member.application.dto.request.SignInRequest;
import com.ops.ops.modules.member.application.dto.request.SignUpRequest;
import com.ops.ops.modules.member.application.dto.response.EmailFindResponse;
import com.ops.ops.modules.member.application.dto.response.SignInResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 기능")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @Operation(summary = "회원가입", description = "회원가입을 합니다. 팀장은 이미 회원가입 되어 있으므로 이메일과 비밀번호만 업데이트합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody final SignUpRequest signUpRequest) {
        memberCommandService.signUp(signUpRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @Operation(summary = "회원가입 이메일 인증", description = "인증 코드를 발급하고, 메일을 전송합니다.")
    @ApiResponse(responseCode = "201", description = "이메일 인증 성공")
    @PostMapping("/sign-up/email-auth")
    public ResponseEntity<Void> signUpEmailAuth(@Valid @RequestBody final EmailAuthRequest emailAuthRequest) {
        memberCommandService.signUpEmailAuth(emailAuthRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @Operation(summary = "회원가입 이메일 인증코드 확인", description = "인증코드를 확인합니다.")
    @ApiResponse(responseCode = "204", description = "확인 성공")
    @PatchMapping("/sign-up/email-auth")
    public ResponseEntity<Void> confirmSignUpEmailAuth(
            @Valid @RequestBody final EmailAuthConfirmRequest emailAuthConfirmRequest) {
        memberCommandService.confirmSignUpEmailAuth(emailAuthConfirmRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @Operation(summary = "로그인", description = "로그인 합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody final SignInRequest signInRequest) {
        final SignInResponse response = memberCommandService.signIn(signInRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경 이메일 인증", description = "인증 코드를 발급하고, 메일을 전송합니다.")
    @ApiResponse(responseCode = "201", description = "이메일 인증 성공")
    @PostMapping("/sign-in/password-reset/email-auth")
    public ResponseEntity<Void> signInEmailAuth(@Valid @RequestBody final EmailAuthRequest emailAuthRequest) {
        memberCommandService.signInEmailAuth(emailAuthRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @Operation(summary = "비밀번호 변경 이메일 인증코드 확인", description = "인증코드를 확인합니다.")
    @ApiResponse(responseCode = "204", description = "확인 성공")
    @PatchMapping("/sign-in/password-reset/email-auth")
    public ResponseEntity<Void> confirmSignInEmailAuth(
            @Valid @RequestBody final EmailAuthConfirmRequest emailAuthConfirmRequest) {
        memberCommandService.confirmSignInEmailAuth(emailAuthConfirmRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    @ApiResponse(responseCode = "204", description = "변경 성공")
    @PatchMapping("/sign-in/password-reset")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody final PasswordUpdateRequest passwordUpdateRequest) {
        memberCommandService.updatePassword(passwordUpdateRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @Operation(summary = "가입 아이디 찾기", description = "학번을 통해 가입 이메일을 찾습니다.")
    @ApiResponse(responseCode = "200", description = "가입 이메일 조회 성공")
    @GetMapping("/sign-in/{studentId}/email-find")
    public ResponseEntity<EmailFindResponse> getMyEmail(@Parameter(description = "학번") @PathVariable String studentId) {
        final EmailFindResponse response = memberQueryService.getMyEmail(studentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구글 로그인 시작", description = "사용자를 구글 OAuth 인증 페이지로 리다이렉트하여 구글 로그인을 시작합니다.")
    @ApiResponse(responseCode = "302", description = "구글 OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/oauth/google")
    public ResponseEntity<Void> googleOAuthRedirect() {
        final String redirectURL = memberCommandService.getGoogleOAuthRedirectURL();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectURL);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @Operation(summary = "구글 로그인 콜백 처리", description = "구글 OAuth 인증 완료 후 콜백을 처리하여 회원가입/로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "구글 로그인 성공")
    @GetMapping("/oauth/google/callback")
    public ResponseEntity<SignInResponse> googleOAuthCallback(final String code) {
        final SignInResponse response = memberCommandService.getGoogleOAuthCallback(code);
        return ResponseEntity.ok(response);
    }
}
