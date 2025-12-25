package kr.ac.jbnu.ksh.blogtp.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank String content
) {}
