package kr.ac.jbnu.ksh.blogtp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank @Size(min = 8, max = 64) String newPassword
) {}
