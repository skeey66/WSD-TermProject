package kr.ac.jbnu.ksh.blogtp.bookmark.service;

import kr.ac.jbnu.ksh.blogtp.bookmark.domain.Bookmark;
import kr.ac.jbnu.ksh.blogtp.bookmark.dto.BookmarkResponse;
import kr.ac.jbnu.ksh.blogtp.bookmark.repository.BookmarkRepository;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.post.repository.PostRepository;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void add(Long postId, AuthPrincipal principal) {
        if (bookmarkRepository.existsByPost_IdAndUser_Id(postId, principal.userId())) {
            return;
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(principal.userId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        bookmarkRepository.save(new Bookmark(post, user));
    }

    @Transactional
    public void remove(Long postId, AuthPrincipal principal) {
        bookmarkRepository.deleteByPost_IdAndUser_Id(postId, principal.userId());
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> myBookmarks(AuthPrincipal principal, Pageable pageable) {
        return bookmarkRepository.findByUser_Id(principal.userId(), pageable).map(BookmarkResponse::from);
    }
}
