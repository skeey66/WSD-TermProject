package kr.ac.jbnu.ksh.blogtp.post.repository;

import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
}
