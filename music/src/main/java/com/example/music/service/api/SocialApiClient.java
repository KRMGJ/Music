package com.example.music.service.api;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.music.model.GoogleToken;
import com.example.music.util.OAuthProperties;

@Service
public class SocialApiClient {

	@Autowired
	OAuthProperties properties;

	public GoogleToken exchangeCodeForGoogleToken(String code) {
		RestTemplate rest = new RestTemplate();
		String url = "https://oauth2.googleapis.com/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", properties.getGoogleClientId());
		params.add("client_secret", properties.getGoogleClientSecret());
		params.add("redirect_uri", properties.getGoogleRedirectUri());
		params.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(params, headers);

		ResponseEntity<Map<String, Object>> res = rest.exchange(url, HttpMethod.POST, req,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});

		Map<String, Object> body = Objects.requireNonNull(res.getBody(), "empty token response");

		return GoogleToken.builder().accessToken((String) body.get("access_token"))
				.refreshToken((String) body.get("refresh_token"))
				.expiresIn(body.get("expires_in") == null ? null : ((Number) body.get("expires_in")).longValue())
				.tokenType((String) body.get("token_type")).scope((String) body.get("scope"))
				.idToken((String) body.get("id_token")).build();
	}

	// --- (B) 구글: refresh_token -> access_token 재발급
	public GoogleToken refreshGoogleAccessToken(String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		RestTemplate rest = new RestTemplate();
		String url = "https://oauth2.googleapis.com/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", properties.getGoogleClientId());
		params.add("client_secret", properties.getGoogleClientSecret());
		params.add("refresh_token", refreshToken);

		HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(params, headers);

		ResponseEntity<Map<String, Object>> res = rest.exchange(url, HttpMethod.POST, req,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});

		Map<String, Object> body = Objects.requireNonNull(res.getBody());

		return GoogleToken.builder().accessToken((String) body.get("access_token")).refreshToken(refreshToken)
				.expiresIn(body.get("expires_in") == null ? null : ((Number) body.get("expires_in")).longValue())
				.tokenType((String) body.get("token_type")).scope((String) body.get("scope")).build();
	}

	@SuppressWarnings("rawtypes")
	public String getAccessTokenFromGoogle(String code) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://oauth2.googleapis.com/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", properties.getGoogleClientId());
		params.add("client_secret", properties.getGoogleClientSecret());
		params.add("redirect_uri", properties.getGoogleRedirectUri());
		params.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		return (String) Objects.requireNonNull(response.getBody()).get("access_token");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getUserInfoFromGoogle(String accessToken) {
		String url = "https://www.googleapis.com/oauth2/v2/userinfo";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<Map> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class);

		return response.getBody();
	}

	@SuppressWarnings("rawtypes")
	public String getAccessTokenFromNaver(String code) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://nid.naver.com/oauth2.0/token";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", properties.getNaverClientId());
		params.add("client_secret", properties.getNaverClientSecret());
		params.add("redirect_uri", properties.getNaverRedirectUri());
		params.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		return (String) Objects.requireNonNull(response.getBody()).get("access_token");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getUserInfoFromNaver(String accessToken) {
		String url = "https://openapi.naver.com/v1/nid/me";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<Map> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class);

		return response.getBody();
	}

	@SuppressWarnings("rawtypes")
	public String getAccessTokenFromKakao(String code) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://kauth.kakao.com/oauth/token";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");

		params.add("client_id", properties.getKakaoClientId());
		params.add("redirect_uri", properties.getKakaoRedirectUri());
		params.add("code", code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		return (String) Objects.requireNonNull(response.getBody()).get("access_token");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> getUserInfoFromKakao(String accessToken) {
		String url = "https://kapi.kakao.com/v2/user/me";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<Map> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class);

		return response.getBody();
	}
}
