package kr.ac.jbnu.ksh.blogtp.user.service;

import kr.ac.jbnu.ksh.blogtp.auth.repository.RefreshTokenRepository;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.domain.UserStatus;
import kr.ac.jbnu.ksh.blogtp.user.dto.UpdatePasswordRequest;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public UserResponse me(AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse changePassword(AuthPrincipal principal, UpdatePasswordRequest req) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.changePasswordHash(passwordEncoder.encode(req.newPassword()));
        return UserResponse.from(user);
    }

    @Transactional
    public void deactivate(AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.changeStatus(UserStatus.SUSPENDED);
        refreshTokenRepository.deleteByUser_Id(user.getId());
    }
}
