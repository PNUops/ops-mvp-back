package com.ops.ops.modules.member.application;

import com.ops.ops.modules.member.domain.Member;
import com.ops.ops.modules.member.exception.MemberException;
import com.ops.ops.modules.member.exception.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {
	public void isAdmin(Member member) {
		if (!member.isAdmin()) {
			throw new MemberException(MemberExceptionType.NOT_ADMIN);
		}
	}
}
