package com.ops.ops.global.util.oauth.exception;

import com.ops.ops.global.base.BaseException;
import com.ops.ops.global.base.BaseExceptionType;

public class OAuthException extends BaseException {

	private final OAuthExceptionType exceptionType;

	public OAuthException(final OAuthExceptionType exceptionType) {
		super(exceptionType.errorMessage());
		this.exceptionType = exceptionType;
	}

	public OAuthException(final OAuthExceptionType exceptionType, final String message) {
		super(message);
		this.exceptionType = exceptionType;
	}

	@Override
	public BaseExceptionType exceptionType() {
		return exceptionType;
	}
}
