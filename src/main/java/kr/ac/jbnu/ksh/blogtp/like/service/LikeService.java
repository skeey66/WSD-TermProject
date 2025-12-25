package kr.ac.jbnu.ksh.blogtp.like.service;

import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.like.domain.PostLike;
import kr.ac.jbnu.ksh.blogtp.like.dto.LikeCountResponse;
import kr.ac.jbnu.ksh.blogtp.like.repository.PostLikeRepository;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.post.repository.PostRepository;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeCountResponse like(Long postId, AuthPrincipal principal) {
        if (postLikeRepository.existsByPost_IdAndUser_Id(postId, principal.userId())) {
            long cnt = postLikeRepository.countByPost_Id(postId);
            return new LikeCountResponse(postId, cnt);
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(principal.userId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        postLikeRepository.save(new PostLike(post, user));
        long cnt = postLikeRepository.countByPost_Id(postId);
        return new LikeCountResponse(postId, cnt);
    }

    @Transactional
    public LikeCountResponse unlike(Long postId, AuthPrincipal principal) {
        postLikeRepository.deleteByPost_IdAndUser_Id(postId, principal.userId());
        long cnt = postLikeRepository.countByPost_Id(postId);
        return new LikeCountResponse(postId, cnt);
    }

    @Transactional(readOnly = true)
    public LikeCountResponse count(Long postId) {
        long cnt = postLikeRepository.countByPost_Id(postId);
        return new LikeCountResponse(postId, cnt);
    }
}
