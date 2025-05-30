package com.ops.ops.modules.member.application;

import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_회원;
import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_PUSAN_UNIVERSITY_EMAIL;
import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_VERIFIED_EMAIL_AUTH;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_EMAIL;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_STUDENT_ID;

import com.ops.ops.global.util.MailUtil;
import com.ops.ops.modules.member.application.dto.request.EmailAuthConfirmRequest;
import com.ops.ops.modules.member.application.dto.request.EmailAuthRequest;
import com.ops.ops.modules.member.application.dto.request.SignUpRequest;
import com.ops.ops.modules.member.domain.EmailAuth;
import com.ops.ops.modules.member.domain.Member;
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

        emailAuthRepository.save(EmailAuth.builder()
                .email(email)
                .token(code)
                .build());
        sendAuthCodeMail(email, code);
    }

    public void confirmSignUpEmailAuth(final EmailAuthConfirmRequest request) {
        final EmailAuth memberEmailAuth = emailAuthRepository.findByEmail(request.email());
        if (memberEmailAuth.getToken().equals(request.authCode())) memberEmailAuth.correct();
        else throw new EmailAuthException(NOT_VERIFIED_EMAIL_AUTH);
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

    void sendAuthCodeMail(final String email, final String authCode) {
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
}
