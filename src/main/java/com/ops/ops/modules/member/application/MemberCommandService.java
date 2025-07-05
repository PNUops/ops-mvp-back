package com.ops.ops.modules.member.application;


import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_회원;
import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_PUSAN_UNIVERSITY_EMAIL;
import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_VERIFIED_EMAIL_AUTH;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_EMAIL;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_STUDENT_ID;
import static com.ops.ops.modules.member.exception.MemberExceptionType.CANNOT_CHANGE_SAME_PASSWORD;
import static com.ops.ops.modules.member.exception.MemberExceptionType.CANNOT_MATCH_PASSWORD;
import static com.ops.ops.modules.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.ops.ops.global.security.JwtProvider;
import com.ops.ops.global.util.MailUtil;
import com.ops.ops.modules.member.application.dto.request.EmailAuthConfirmRequest;
import com.ops.ops.modules.member.application.dto.request.EmailAuthRequest;
import com.ops.ops.modules.member.application.dto.request.PasswordUpdateRequest;
import com.ops.ops.modules.member.application.dto.request.SignInRequest;
import com.ops.ops.modules.member.application.dto.request.SignUpRequest;
import com.ops.ops.modules.member.application.dto.response.SignInResponse;
import com.ops.ops.modules.member.domain.EmailAuth;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.MemberRoleType;
import com.ops.ops.modules.member.domain.dao.EmailAuthRepository;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.EmailAuthException;
import com.ops.ops.modules.member.exception.MemberException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailUtil mailUtil;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int AUTH_CODE_LENGTH = 10;
    private static final char[] AUTH_CODE_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public void signUp(final SignUpRequest request) {
        final String encodingPassword = passwordEncoder.encode(request.password());
        final EmailAuth emailAuth = checkEmailAuth(request.email());
        checkIsDuplicateEmail(request.email());

        memberRepository.findByStudentIdAndName(request.studentId(), request.name())
                .ifPresentOrElse(
                        member -> member.updateTeamLeaderInfo(request.email(), encodingPassword),
                        () -> registerNewMember(request.name(), request.studentId(), request.email(), encodingPassword)
                );

        emailAuthRepository.delete(emailAuth);
    }

    public void signUpEmailAuth(final EmailAuthRequest request) {
        final String email = request.email();
        validatePusanDomain(email);
        final String code = generateRandomAuthCode();

        saveAuthCode(email, code);
        sendAuthCodeMail(email, code);
    }

    public void confirmSignUpEmailAuth(final EmailAuthConfirmRequest request) {
        final EmailAuth memberEmailAuth = emailAuthRepository.findByEmail(request.email());
        if (memberEmailAuth.getToken().equals(request.authCode())) {
            memberEmailAuth.correct();
        } else {
            throw new EmailAuthException(NOT_VERIFIED_EMAIL_AUTH);
        }
    }

    public SignInResponse signIn(final SignInRequest request) {
        final Member member = getValidateExistMember(request.email());
        checkCorrectPassword(member.getPassword(), request.password());
        final List<String> roles = member.getRoles().stream()
                .map(MemberRoleType::toString)
                .toList();
        final String token = jwtProvider.createToken(String.valueOf(member.getId()), roles, member.getName());
        return SignInResponse.from(member, token);
    }

    public void signInEmailAuth(final EmailAuthRequest request) {
        validateExistMember(request.email());
        signUpEmailAuth(request);
    }

    public void confirmSignInEmailAuth(final EmailAuthConfirmRequest request) {
        validateExistMember(request.email());
        confirmSignUpEmailAuth(request);
    }

    public void updatePassword(final PasswordUpdateRequest request) {
        final Member member = getValidateExistMember(request.email());
        final EmailAuth emailAuth = checkEmailAuth(request.email());

        checkEqualPassword(request.newPassword(), member);
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
        emailAuthRepository.delete(emailAuth);
    }

    private void registerNewMember(final String name, final String studentId, final String email,
                                   final String password) {
        checkIsDuplicateStudentId(studentId);

        memberRepository.save(Member.builder()
                .name(name)
                .studentId(studentId)
                .email(email)
                .password(password)
                .roles(Set.of(ROLE_회원))
                .build());
    }

    private EmailAuth checkEmailAuth(final String email) {
        final EmailAuth memberEmailAuth = emailAuthRepository.findByEmail(email);
        if (!memberEmailAuth.getIsCorrected()) {
            throw new EmailAuthException(NOT_VERIFIED_EMAIL_AUTH);
        }
        return memberEmailAuth;
    }

    private void checkIsDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ALREADY_EXIST_EMAIL);
        }
    }

    private void checkIsDuplicateStudentId(final String studentId) {
        if (memberRepository.existsByStudentId(studentId)) {
            throw new MemberException(ALREADY_EXIST_STUDENT_ID);
        }
    }

    private static String generateRandomAuthCode() {
        char[] buf = new char[AUTH_CODE_LENGTH];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = AUTH_CODE_POOL[SECURE_RANDOM.nextInt(AUTH_CODE_POOL.length)];
        }
        return new String(buf);
    }

    private void sendAuthCodeMail(final String email, final String authCode) {
        final List<String> userList = new ArrayList<>(List.of(email));
        final String subject = "SW 성과관리시스템 인증코드 발송 메일입니다.";
        final String text = "인증코드는 " + authCode + " 입니다.";
        mailUtil.sendMail(userList, subject, text);
    }

    private void validatePusanDomain(final String email) {
        if (!email.endsWith("@pusan.ac.kr")) {
            throw new EmailAuthException(NOT_PUSAN_UNIVERSITY_EMAIL);
        }
    }

    private Member getValidateExistMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void validateExistMember(final String email) {
        memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void checkCorrectPassword(final String savePassword, final String inputPassword) {
        if (!passwordEncoder.matches(inputPassword, savePassword)) {
            throw new MemberException(CANNOT_MATCH_PASSWORD);
        }
    }

    private void saveAuthCode(final String email, final String code) {
        if (emailAuthRepository.existsByEmail(email)) {
            final EmailAuth emailAuth = emailAuthRepository.findByEmail(email);
            emailAuth.updateAuthCode(code);
        } else {
            emailAuthRepository.save(EmailAuth.builder()
                    .email(email)
                    .token(code)
                    .build());
        }
    }

    private void checkEqualPassword(final String newPassword, final Member member) {
        if (member.isEqual(newPassword)) {
            throw new MemberException(CANNOT_CHANGE_SAME_PASSWORD);
        }
    }
}
