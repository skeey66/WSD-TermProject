package kr.ac.jbnu.ksh.blogtp.comment.service;

import kr.ac.jbnu.ksh.blogtp.comment.domain.Comment;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentCreateRequest;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentResponse;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentUpdateRequest;
import kr.ac.jbnu.ksh.blogtp.comment.repository.CommentRepository;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.post.repository.PostRepository;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.user.domain.Role;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long postId, AuthPrincipal principal, CommentCreateRequest req) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        User author = userRepository.findById(principal.userId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        Comment c = new Comment(post, author, req.content());
        return CommentResponse.from(commentRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> listByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPost_Id(postId, pageable).map(CommentResponse::from);
    }

    @Transactional
    public CommentResponse update(Long commentId, AuthPrincipal principal, CommentUpdateRequest req) {
        Comment c = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkOwnerOrAdmin(c, principal);
        c.edit(req.content());
        return CommentResponse.from(c);
    }

    @Transactional
    public void delete(Long commentId, AuthPrincipal principal) {
        Comment c = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        checkOwnerOrAdmin(c, principal);
        commentRepository.delete(c);
    }

    private void checkOwnerOrAdmin(Comment c, AuthPrincipal principal) {
        boolean isOwner = c.getAuthor().getId().equals(principal.userId());
        boolean isAdmin = Role.ROLE_ADMIN.name().equals(principal.role());
        if (!isOwner && !isAdmin) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }
}
