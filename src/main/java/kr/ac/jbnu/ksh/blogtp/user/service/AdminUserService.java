package kr.ac.jbnu.ksh.blogtp.user.service;

import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.user.domain.Role;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.domain.UserStatus;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Transactional
    public UserResponse changeRole(Long userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.changeRole(role);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse changeStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.changeStatus(status);
        return UserResponse.from(user);
    }
}
