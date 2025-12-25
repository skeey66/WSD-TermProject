package kr.ac.jbnu.ksh.blogtp.like.repository;

import kr.ac.jbnu.ksh.blogtp.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    long deleteByPost_IdAndUser_Id(Long postId, Long userId);
    long countByPost_Id(Long postId);
}
