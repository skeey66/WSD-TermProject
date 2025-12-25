package kr.ac.jbnu.ksh.blogtp.auth.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.auth.dto.*;
import kr.ac.jbnu.ksh.blogtp.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("${app.api-prefix:/api}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새 사용자를 등록하고 Access/Refresh 토큰을 발급합니다.")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse signup(@Valid @RequestBody SignupRequest req) {
        return authService.signup(req);
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 Access/Refresh 토큰을 발급합니다.")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return authService.refresh(req);
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 폐기(무효화)합니다.")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req);
    }

    @PostMapping("/login/google")
    @Operation(
            summary = "구글 소셜 로그인",
            description = "Google ID Token을 검증한 뒤(환경변수 GOOGLE_CLIENT_ID 필요) 사용자 가입/로그인을 처리하고 Access/Refresh 토큰을 발급합니다. 토큰이 유효하지 않으면 401을 반환합니다."
    )
    public TokenResponse loginGoogle(@Valid @RequestBody SocialLoginRequest req) {
        return authService.loginWithGoogle(req);
    }

    @PostMapping("/login/firebase")
    @Operation(
            summary = "Firebase 소셜 로그인",
            description = "Firebase ID Token을 검증한 뒤(FIREBASE_ENABLED=true 및 인증키 설정 필요) 사용자 가입/로그인을 처리하고 Access/Refresh 토큰을 발급합니다. 토큰이 유효하지 않으면 401을 반환합니다."
    )
    public TokenResponse loginFirebase(@Valid @RequestBody SocialLoginRequest req) {
        return authService.loginWithFirebase(req);
    }
}
