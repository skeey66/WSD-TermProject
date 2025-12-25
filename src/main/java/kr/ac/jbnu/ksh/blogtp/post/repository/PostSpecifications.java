package kr.ac.jbnu.ksh.blogtp.post.repository;

import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecifications {

    public static Specification<Post> keyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("content")), like)
                );
    }

    public static Specification<Post> categoryId(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Post> authorId(Long authorId) {
        if (authorId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Post> published(Boolean published) {
        if (published == null) return null;
        return (root, query, cb) -> cb.equal(root.get("published"), published);
    }
}
