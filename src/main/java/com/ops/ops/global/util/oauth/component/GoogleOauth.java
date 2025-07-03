package com.ops.ops.global.util.oauth.component;

import static com.ops.ops.global.util.oauth.exception.OAuthExceptionType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.ops.global.util.oauth.exception.OAuthException;
import com.ops.ops.global.util.oauth.dto.GoogleOAuthToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

	@Value("${spring.oauth2.google.url}")
	private String GOOGLE_SNS_URL;

	@Value("${spring.oauth2.google.client-id}")
	private String GOOGLE_SNS_CLIENT_ID;

	@Value("${spring.oauth2.google.callback-login-url}")
	private String GOOGLE_SNS_CALLBACK_LOGIN_URL;

	@Value("${spring.oauth2.google.client-secret}")
	private String GOOGLE_SNS_CLIENT_SECRET;

	@Value("${spring.oauth2.google.scope}")
	private String GOOGLE_DATA_ACCESS_SCOPE;

	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	@Override
	public String getOauthRedirectURL() {
		Map<String, Object> params = new HashMap<>();
		params.put("scope", GOOGLE_DATA_ACCESS_SCOPE);
		params.put("response_type", "code");
		params.put("client_id", GOOGLE_SNS_CLIENT_ID);
		params.put("redirect_uri", GOOGLE_SNS_CALLBACK_LOGIN_URL);

		String parameterString = params.entrySet().stream()
			.map(x -> x.getKey() + "=" + x.getValue())
			.collect(Collectors.joining("&"));
		String redirectURL = GOOGLE_SNS_URL + "?" + parameterString;
		log.info("구글 소셜 OAuth Redirect URL 확인용 : {}", redirectURL);

		return redirectURL;
	}

	@Override
	public <T> T getUserInfoByCode(String code, Class<T> userType) throws JsonProcessingException {
		ResponseEntity<String> requestAccessToken = requestAccessToken(code);
		GoogleOAuthToken oAuthToken = getAccessToken(requestAccessToken);
		ResponseEntity<String> userInfo = requestUserInfo(oAuthToken);
		return getUserInfo(userInfo, userType);
	}

	private ResponseEntity<String> requestAccessToken(String code) {
		String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", GOOGLE_SNS_CLIENT_ID);
		params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET);
		params.add("redirect_uri", GOOGLE_SNS_CALLBACK_LOGIN_URL);
		params.add("grant_type", "authorization_code");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, requestEntity, String.class);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity;
			} else {
				log.error("Google Access Token Request Failed: {}", responseEntity.getBody());
				throw new OAuthException(SOCIAL_LOGIN_FAILED_AUTH_CODE);
			}
		} catch (RestClientException e) {
			log.error("Google Access Token Request Server Error: {}", e.getMessage());
			throw new OAuthException(SOCIAL_LOGIN_SERVER_ERROR);
		}
	}

	private GoogleOAuthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
		try {
			GoogleOAuthToken oAuthToken = objectMapper.readValue(response.getBody(), GoogleOAuthToken.class);
			if (oAuthToken == null || oAuthToken.access_token() == null) {
				throw new OAuthException(FAILED_TO_GET_ACCESS_TOKEN);
			}
			return oAuthToken;
		} catch (JsonProcessingException e) {
			log.error("Failed to parse Google OAuth Token: {}", e.getMessage());
			throw new OAuthException(FAILED_TO_GET_ACCESS_TOKEN);
		}
	}

	private ResponseEntity<String> requestUserInfo(GoogleOAuthToken oAuthToken) {
		String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + oAuthToken.access_token());
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
			return response;
		} catch (RestClientException e) {
			log.error("Google User Info Request Server Error: {}", e.getMessage());
			throw new OAuthException(FAILED_TO_GET_SOCIAL_USER_INFO);
		}
	}

	private <T> T getUserInfo(ResponseEntity<String> userInfoRes, Class<T> userType) throws JsonProcessingException {
		try {
			T googleUser = objectMapper.readValue(userInfoRes.getBody(), userType);
			if (googleUser == null) {
				throw new OAuthException(FAILED_TO_GET_SOCIAL_USER_INFO);
			}
			return googleUser;
		} catch (JsonProcessingException e) {
			log.error("Failed to parse Google User Info: {}", e.getMessage());
			throw new OAuthException(FAILED_TO_GET_SOCIAL_USER_INFO);
		}
	}
}
