package kr.ac.jbnu.ksh.blogtp.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final ObjectMapper objectMapper;

    @Value("${app.social.google.tokeninfo-url}")
    private String tokenInfoUrl;

    @Value("${app.social.google.client-id:}")
    private String googleClientId;

    @Value("${app.social.firebase.enabled:false}")
    private boolean firebaseEnabled;

    /**
     * Google ID Token 검증: tokeninfo endpoint로 email/aud 확인.
     * (강의/과제 환경에서 구현 난이도 낮고, 테스트가 쉬운 방식)
     */
    public String verifyGoogleAndGetEmail(String idToken) {
        try {
            String url = tokenInfoUrl + "?id_token=" + idToken;
            String body = RestClient.create()
                    .get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            JsonNode json = objectMapper.readTree(body);
            String aud = json.path("aud").asText();
            String email = json.path("email").asText();
            boolean verified = json.path("email_verified").asBoolean(false) || "true".equalsIgnoreCase(json.path("email_verified").asText());

            if (email == null || email.isBlank()) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "Google token에 email이 없습니다.");
            }
            if (!verified) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "email_verified=false");
            }
            if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals(aud)) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "aud(client_id) 불일치");
            }
            return email;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Google token 검증 실패");
        }
    }

    /**
     * Firebase ID Token 검증 (Firebase Admin SDK)
     */
    public String verifyFirebaseAndGetEmail(String idToken) {
        if (!firebaseEnabled) {
            throw new ApiException(ErrorCode.FEATURE_NOT_CONFIGURED, "Firebase 로그인 비활성화 상태입니다.");
        }
        try {
            var decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decoded.getEmail();
            if (email == null || email.isBlank()) {
                throw new ApiException(ErrorCode.UNAUTHORIZED, "Firebase token에 email이 없습니다.");
            }
            return email;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Firebase token 검증 실패");
        }
    }
}
