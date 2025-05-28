package com.ops.ops.modules.member.domain.dao;

import com.ops.ops.modules.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
