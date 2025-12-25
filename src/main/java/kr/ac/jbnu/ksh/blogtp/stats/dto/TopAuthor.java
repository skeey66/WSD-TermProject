package kr.ac.jbnu.ksh.blogtp.stats.dto;

import kr.ac.jbnu.ksh.blogtp.stats.dto.TopAuthorResponse;

/**
 * Spring Data projection for native stats queries.
 *
 * <p>Must match the column aliases in {@code StatsRepository} (email, posts).
 */
public interface TopAuthor {
    String getEmail();

    Long getPosts();

    /** Convenience mapper used by {@code StatsService}. */
    default TopAuthorResponse toResponse() {
        return new TopAuthorResponse(
                getEmail(),
                getPosts() == null ? 0L : getPosts()
        );
    }
}
