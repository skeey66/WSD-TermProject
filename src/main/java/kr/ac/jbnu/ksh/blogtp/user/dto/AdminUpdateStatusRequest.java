package kr.ac.jbnu.ksh.blogtp.user.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.jbnu.ksh.blogtp.user.domain.UserStatus;

public record AdminUpdateStatusRequest(
        @NotNull UserStatus status
) {}
