package com.ops.ops.modules.member.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.ops.ops.modules.member.application.MemberCommandService;
import com.ops.ops.modules.member.application.dto.request.EmailAuthConfirmRequest;
import com.ops.ops.modules.member.application.dto.request.EmailAuthRequest;
import com.ops.ops.modules.member.application.dto.request.SignInRequest;
import com.ops.ops.modules.member.application.dto.request.SignUpRequest;
import com.ops.ops.modules.member.application.dto.response.SignInResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody final SignUpRequest signUpRequest) {
        memberCommandService.signUp(signUpRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/sign-up/email-auth")
    public ResponseEntity<Void> signUpEmailAuth(@Valid @RequestBody final EmailAuthRequest emailAuthRequest) {
        memberCommandService.signUpEmailAuth(emailAuthRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/sign-up/email-auth")
    public ResponseEntity<Void> confirmSignUpEmailAuth(
            @Valid @RequestBody final EmailAuthConfirmRequest emailAuthConfirmRequest) {
        memberCommandService.confirmSignUpEmailAuth(emailAuthConfirmRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody final SignInRequest signInRequest) {
        final SignInResponse response = memberCommandService.signIn(signInRequest);
        return ResponseEntity.ok(response);
    }
}
