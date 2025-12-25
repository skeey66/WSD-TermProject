package kr.ac.jbnu.ksh.blogtp.stats.repository;

import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.stats.dto.DailyCount;
import kr.ac.jbnu.ksh.blogtp.stats.dto.TopAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * StatsRepository must be a concrete Spring Data JPA repository.
 *
 * We purposely extend JpaRepository<Post, Long> so Spring can safely assign this repository
 * to the JPA store even when multiple Spring Data modules (JPA + Redis) are on the classpath.
 */
public interface StatsRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
                    SELECT DATE(p.created_at) AS date, COUNT(*) AS count
                    FROM posts p
                    WHERE p.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL :days DAY)
                    GROUP BY DATE(p.created_at)
                    ORDER BY date
                    """,
            nativeQuery = true
    )
    List<DailyCount> dailyPosts(@Param("days") int days);

    @Query(
            value = """
                    SELECT u.email AS email, COUNT(*) AS posts
                    FROM users u
                    JOIN posts p ON p.user_id = u.id
                    WHERE p.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL :days DAY)
                    GROUP BY u.email
                    ORDER BY posts DESC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<TopAuthor> topAuthors(@Param("days") int days, @Param("limit") int limit);
}
