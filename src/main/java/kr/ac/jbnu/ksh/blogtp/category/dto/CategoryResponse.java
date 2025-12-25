package kr.ac.jbnu.ksh.blogtp.category.dto;

import kr.ac.jbnu.ksh.blogtp.category.domain.Category;

import java.time.OffsetDateTime;

public record CategoryResponse(
        Long id,
        String name,
        OffsetDateTime createdAt
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getCreatedAt());
    }
}
