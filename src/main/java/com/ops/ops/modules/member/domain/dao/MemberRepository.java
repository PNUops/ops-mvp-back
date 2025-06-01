package com.ops.ops.modules.member.domain.dao;

import com.ops.ops.modules.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(final String email);

	Optional<Member> findByStudentIdAndName(final String studentId, final String name);

	Boolean existsByEmail(final String Email);

	Boolean existsByStudentId(final String studentId);

	@Query("SELECT m.name FROM Member m WHERE m.id = :id")
	String findNameById(@Param("id") Long id);
}
