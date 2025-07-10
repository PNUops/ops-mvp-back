package com.ops.ops.modules.member.domain.dao;

import com.ops.ops.modules.member.domain.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    EmailAuth findByEmail(final String email);

    boolean existsByEmail(final String email);
}
