package kr.ac.jbnu.ksh.blogtp.comment.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentCreateRequest;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentResponse;
import kr.ac.jbnu.ksh.blogtp.comment.dto.CommentUpdateRequest;
import kr.ac.jbnu.ksh.blogtp.comment.service.CommentService;
import kr.ac.jbnu.ksh.blogtp.common.dto.PageResponse;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Comments", description = "댓글 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다. (로그인 필요)")
    @PostMapping("${app.api-prefix:/api}/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest req) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return commentService.create(postId, p, req);
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다.")
    @GetMapping("${app.api-prefix:/api}/posts/{postId}/comments")
    public PageResponse<CommentResponse> list(@PathVariable Long postId, @PageableDefault(sort = "createdAt") Pageable pageable) {
        Page<CommentResponse> page = commentService.listByPost(postId, pageable);
        return PageResponse.from(page);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다. (작성자 또는 관리자)")
    @PutMapping("${app.api-prefix:/api}/comments/{commentId}")
    public CommentResponse update(@PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest req) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return commentService.update(commentId, p, req);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (작성자 또는 관리자)")
    @DeleteMapping("${app.api-prefix:/api}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        commentService.delete(commentId, p);
    }
}
