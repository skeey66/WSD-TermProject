package kr.ac.jbnu.ksh.blogtp.post.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.common.dto.PageResponse;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostCreateRequest;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostResponse;
import kr.ac.jbnu.ksh.blogtp.post.dto.PostUpdateRequest;
import kr.ac.jbnu.ksh.blogtp.post.service.PostService;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Posts", description = "게시글 API")
@RestController
@RequestMapping("${app.api-prefix:/api}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다. (로그인 필요)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@Valid @RequestBody PostCreateRequest req) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return postService.create(p, req);
    }

    @Operation(summary = "게시글 목록 조회", description = "조건(검색/정렬/페이지네이션)으로 게시글 목록을 조회합니다.")
    @GetMapping
    public PageResponse<PostResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Boolean published,
            @PageableDefault(sort = "createdAt") Pageable pageable
    ) {
        Page<PostResponse> page = postService.list(keyword, categoryId, authorId, published, pageable);
        return PageResponse.from(page);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id, Authentication authentication) {
        AuthPrincipal p = (authentication != null && authentication.getPrincipal() instanceof AuthPrincipal ap) ? ap : null;
        return postService.get(id, p);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다. (작성자 또는 관리자)")
    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest req) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return postService.update(id, p, req);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. (작성자 또는 관리자)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        postService.delete(id, p);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 발행", description = "게시글을 발행 상태로 변경합니다. (작성자 또는 관리자)")
    @PostMapping("/{id}/publish")
    public PostResponse publish(@PathVariable Long id) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return postService.publish(id, p);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 발행 취소", description = "게시글의 발행을 취소합니다. (작성자 또는 관리자)")
    @PostMapping("/{id}/unpublish")
    public PostResponse unpublish(@PathVariable Long id) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return postService.unpublish(id, p);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "내 게시글 목록", description = "내가 작성한 게시글 목록을 조회합니다. (로그인 필요)")
    @GetMapping("/me")
    public PageResponse<PostResponse> myPosts(@PageableDefault(sort = "createdAt") Pageable pageable) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return PageResponse.from(postService.myPosts(p, pageable));
    }
}
