package kr.ac.jbnu.ksh.blogtp.user.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.jbnu.ksh.blogtp.user.domain.Role;

public record AdminUpdateRoleRequest(
        @NotNull Role role
) {}
