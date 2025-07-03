package com.ops.ops.global.util.oauth.token;

public record GoogleOAuthToken(
	String access_token,
	Integer expires_in,
	String refresh_token
) {
}
