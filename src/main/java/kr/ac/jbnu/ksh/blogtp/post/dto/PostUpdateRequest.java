package kr.ac.jbnu.ksh.blogtp.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateRequest(
        Long categoryId,
        @NotBlank @Size(min = 1, max = 120) String title,
        @NotBlank String content
) {}
