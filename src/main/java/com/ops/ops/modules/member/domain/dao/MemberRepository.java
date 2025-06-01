package com.ops.ops.modules.member.domain.dao;

import com.ops.ops.modules.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(final String email);

    Optional<Member> findByStudentIdAndName(final String studentId, final String name);

    Boolean existsByEmail(final String Email);

    Boolean existsByStudentId(final String studentId);
}
