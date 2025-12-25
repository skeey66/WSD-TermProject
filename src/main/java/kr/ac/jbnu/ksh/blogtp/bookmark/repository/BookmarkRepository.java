package kr.ac.jbnu.ksh.blogtp.bookmark.repository;

import kr.ac.jbnu.ksh.blogtp.bookmark.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    long deleteByPost_IdAndUser_Id(Long postId, Long userId);
    Page<Bookmark> findByUser_Id(Long userId, Pageable pageable);
}
