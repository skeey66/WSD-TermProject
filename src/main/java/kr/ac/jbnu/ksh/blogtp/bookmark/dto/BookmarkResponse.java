package kr.ac.jbnu.ksh.blogtp.bookmark.dto;

import kr.ac.jbnu.ksh.blogtp.bookmark.domain.Bookmark;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostResponse;

import java.time.OffsetDateTime;

public record BookmarkResponse(
        Long id,
        PostResponse post,
        OffsetDateTime createdAt
) {
    public static BookmarkResponse from(Bookmark b) {
        return new BookmarkResponse(b.getId(), PostResponse.from(b.getPost()), b.getCreatedAt());
    }
}
