package com.zinier.capacity_planner.security;

import com.zinier.capacity_planner.security.model.LoginResponseModel;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class SsoAuthControllerV1 {

    private final JwtUtilV1 jwtUtil;

    @Value("${okta.issuer-uri}")
    private String issuerUri;

    @Value("${okta.client-id}")
    private String clientId;

    @Value("${okta.client-secret}")
    private String clientSecret;

    @Value("${okta.redirect-uri}")
    private String redirectUri;

    @PostMapping("/sso/callback")
    public LoginResponseModel ssoCallback(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Authorization code is required");
        }

        // Exchange authorization code for tokens with Okta
        Map<String, Object> tokenResponse = exchangeCodeForTokens(code);

        // Extract email from the ID token
        String idToken = (String) tokenResponse.get("id_token");
        String email = extractEmailFromIdToken(idToken);

        // Okta authentication is the source of truth — no DB lookup needed
        String username = email.split("@")[0];
        String role = AppUserEntity.UserRole.ADMIN.name();
        String token = jwtUtil.generateToken(username, role);

        return LoginResponseModel.builder()
                .token(token)
                .username(username)
                .fullName(username)
                .role(role)
                .build();
    }

    private Map<String, Object> exchangeCodeForTokens(String code) {
        String tokenEndpoint = issuerUri + "/oauth2/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to exchange authorization code with Okta");
        }

        return response.getBody();
    }

    private String extractEmailFromIdToken(String idToken) {
        try {
            // Decode the JWT payload (Okta ID tokens are JWTs)
            String[] parts = idToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            // Parse the JSON payload to extract email
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);

            String email = (String) claims.get("email");
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email not found in Okta ID token");
            }
            return email;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Okta ID token", e);
        }
    }
}
