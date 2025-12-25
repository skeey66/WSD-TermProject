package kr.ac.jbnu.ksh.blogtp.post.service;

import kr.ac.jbnu.ksh.blogtp.category.domain.Category;
import kr.ac.jbnu.ksh.blogtp.category.service.CategoryService;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostCreateRequest;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostResponse;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostUpdateRequest;
import kr.ac.jbnu.ksh.blogtp.post.repository.PostRepository;
import kr.ac.jbnu.ksh.blogtp.post.repository.PostSpecifications;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.user.domain.Role;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import kr.ac.jbnu.ksh.blogtp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Transactional
    public PostResponse create(AuthPrincipal principal, PostCreateRequest req) {
        User author = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        Category category = categoryService.getEntityOrNull(req.categoryId());
        Post post = new Post(author, category, req.title(), req.content());
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> list(String keyword, Long categoryId, Long authorId, Boolean published, Pageable pageable) {
        Specification<Post> spec = Specification.where(PostSpecifications.keyword(keyword))
                .and(PostSpecifications.categoryId(categoryId))
                .and(PostSpecifications.authorId(authorId))
                .and(PostSpecifications.published(published));
        return postRepository.findAll(spec, pageable).map(PostResponse::from);
    }

    @Transactional(readOnly = true)
    public PostResponse get(Long id, AuthPrincipal principalOrNull) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        if (!post.isPublished()) {
            if (principalOrNull == null) {
                throw new ApiException(ErrorCode.POST_NOT_FOUND); // hide unpublished
            }
            boolean isOwner = post.getAuthor().getId().equals(principalOrNull.userId());
            boolean isAdmin = Role.ROLE_ADMIN.name().equals(principalOrNull.role());
            if (!isOwner && !isAdmin) {
                throw new ApiException(ErrorCode.POST_NOT_FOUND);
            }
        }
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long id, AuthPrincipal principal, PostUpdateRequest req) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        checkOwnerOrAdmin(post, principal);

        Category category = categoryService.getEntityOrNull(req.categoryId());
        post.update(category, req.title(), req.content());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long id, AuthPrincipal principal) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        checkOwnerOrAdmin(post, principal);
        postRepository.delete(post);
    }

    @Transactional
    public PostResponse publish(Long id, AuthPrincipal principal) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        checkOwnerOrAdmin(post, principal);
        if (post.isPublished()) {
            throw new ApiException(ErrorCode.STATE_CONFLICT, "이미 publish 상태입니다.");
        }
        post.publish();
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse unpublish(Long id, AuthPrincipal principal) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
        checkOwnerOrAdmin(post, principal);
        if (!post.isPublished()) {
            throw new ApiException(ErrorCode.STATE_CONFLICT, "이미 unpublish 상태입니다.");
        }
        post.unpublish();
        return PostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> myPosts(AuthPrincipal principal, Pageable pageable) {
        return list(null, null, principal.userId(), null, pageable);
    }

    private void checkOwnerOrAdmin(Post post, AuthPrincipal principal) {
        boolean isOwner = post.getAuthor().getId().equals(principal.userId());
        boolean isAdmin = Role.ROLE_ADMIN.name().equals(principal.role());
        if (!isOwner && !isAdmin) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }
}
