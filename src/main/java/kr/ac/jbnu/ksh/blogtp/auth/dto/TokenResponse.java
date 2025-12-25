package kr.ac.jbnu.ksh.blogtp.auth.dto;

import java.time.OffsetDateTime;

public record TokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        OffsetDateTime refreshExpiresAt,
        String role
) {
    public static TokenResponse bearer(String accessToken, String refreshToken, OffsetDateTime refreshExpiresAt, String role) {
        return new TokenResponse("Bearer", accessToken, refreshToken, refreshExpiresAt, role);
    }
}
