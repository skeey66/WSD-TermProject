package kr.ac.jbnu.ksh.blogtp.comment.repository;

import kr.ac.jbnu.ksh.blogtp.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost_Id(Long postId, Pageable pageable);
}
