package kr.ac.jbnu.ksh.blogtp.user.dto;

import kr.ac.jbnu.ksh.blogtp.user.domain.Role;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.domain.UserStatus;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String email,
        Role role,
        UserStatus status,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getStatus(), u.getCreatedAt());
    }
}
