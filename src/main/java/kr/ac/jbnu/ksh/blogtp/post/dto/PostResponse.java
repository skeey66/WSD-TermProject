package kr.ac.jbnu.ksh.blogtp.post.dto;

import kr.ac.jbnu.ksh.blogtp.category.dto.CategoryResponse;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;

import java.time.OffsetDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        boolean published,
        UserResponse author,
        CategoryResponse category,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static PostResponse from(Post p) {
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.isPublished(),
                UserResponse.from(p.getAuthor()),
                p.getCategory() != null ? CategoryResponse.from(p.getCategory()) : null,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
