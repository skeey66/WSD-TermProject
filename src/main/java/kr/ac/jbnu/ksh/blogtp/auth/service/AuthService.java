package kr.ac.jbnu.ksh.blogtp.auth.service;

import kr.ac.jbnu.ksh.blogtp.auth.domain.RefreshToken;
import kr.ac.jbnu.ksh.blogtp.auth.dto.*;
import kr.ac.jbnu.ksh.blogtp.auth.repository.RefreshTokenRepository;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.JwtTokenProvider;
import kr.ac.jbnu.ksh.blogtp.user.domain.Role;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.domain.UserStatus;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialAuthService socialAuthService;

    @Value("${app.jwt.refresh-days}")
    private long refreshDays;

    @Transactional
    public TokenResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 이메일입니다.");
        }
        User user = new User(req.email(), passwordEncoder.encode(req.password()), Role.ROLE_USER);
        userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(ErrorCode.FORBIDDEN, "정지된 계정입니다.");
        }
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse loginWithGoogle(SocialLoginRequest req) {
        String email = socialAuthService.verifyGoogleAndGetEmail(req.idToken());
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(
                new User(email, passwordEncoder.encode(UUID.randomUUID().toString()), Role.ROLE_USER)
        ));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(ErrorCode.FORBIDDEN, "정지된 계정입니다.");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse loginWithFirebase(SocialLoginRequest req) {
        String email = socialAuthService.verifyFirebaseAndGetEmail(req.idToken());
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(
                new User(email, passwordEncoder.encode(UUID.randomUUID().toString()), Role.ROLE_USER)
        ));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(ErrorCode.FORBIDDEN, "정지된 계정입니다.");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest req) {
        RefreshToken rt = refreshTokenRepository.findByToken(req.refreshToken())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "refresh token이 유효하지 않습니다."));
        if (rt.isExpired() || rt.isRevoked()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "refresh token 만료/폐기");
        }
        User user = rt.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(ErrorCode.FORBIDDEN, "정지된 계정입니다.");
        }

        // rotate
        rt.revoke();
        String newToken = UUID.randomUUID().toString();
        OffsetDateTime exp = OffsetDateTime.now().plusDays(refreshDays);
        refreshTokenRepository.save(new RefreshToken(user, newToken, exp));

        String access = jwtTokenProvider.createAccessToken(new AuthPrincipal(user.getId(), user.getEmail(), user.getRole().name()));
        return TokenResponse.bearer(access, newToken, exp, user.getRole().name());
    }

    @Transactional
    public void logout(LogoutRequest req) {
        refreshTokenRepository.deleteByToken(req.refreshToken());
    }

    private TokenResponse issueTokens(User user) {
        String refresh = UUID.randomUUID().toString();
        OffsetDateTime exp = OffsetDateTime.now().plusDays(refreshDays);
        refreshTokenRepository.save(new RefreshToken(user, refresh, exp));

        String access = jwtTokenProvider.createAccessToken(new AuthPrincipal(user.getId(), user.getEmail(), user.getRole().name()));
        return TokenResponse.bearer(access, refresh, exp, user.getRole().name());
    }
}
