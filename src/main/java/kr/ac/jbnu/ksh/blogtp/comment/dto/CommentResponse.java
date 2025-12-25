package kr.ac.jbnu.ksh.blogtp.comment.dto;

import kr.ac.jbnu.ksh.blogtp.comment.domain.Comment;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;

import java.time.OffsetDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        UserResponse author,
        OffsetDateTime createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getPost().getId(), c.getContent(), UserResponse.from(c.getAuthor()), c.getCreatedAt());
    }
}
