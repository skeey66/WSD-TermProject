package kr.ac.jbnu.ksh.blogtp.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank String idToken
) {}
