package com.ops.ops.modules.member.application;

import static com.ops.ops.modules.member.domain.MemberRoleType.ROLE_회원;
import static com.ops.ops.modules.member.exception.EmailAuthExceptionType.NOT_VERIFIED_EMAIL_AUTH;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_EMAIL;
import static com.ops.ops.modules.member.exception.MemberExceptionType.ALREADY_EXIST_STUDENT_ID;

import com.ops.ops.modules.member.application.dto.request.SignUpRequest;
import com.ops.ops.modules.member.domain.EmailAuth;
import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.domain.dao.EmailAuthRepository;
import com.ops.ops.modules.member.domain.dao.MemberRepository;
import com.ops.ops.modules.member.exception.EmailAuthException;
import com.ops.ops.modules.member.exception.MemberException;
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

    public void signUp(final SignUpRequest request) {
        final String encodingPassword = passwordEncoder.encode(request.password());
        final EmailAuth emailAuth = checkEmailAuth(request.email());

        memberRepository.findByStudentIdAndName(request.studentId(), request.name())
                .ifPresentOrElse(
                        member -> member.updateTeamLeaderInfo(request.email(), encodingPassword),
                        () -> registerNewMember(request.name(), request.studentId(), request.email(), encodingPassword)
                );

        emailAuthRepository.delete(emailAuth);
    }

    private void registerNewMember(final String name, final String studentId, final String email,
                                   final String password) {
        checkIsDuplicateEmail(email);
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
}
