package kr.ac.jbnu.ksh.blogtp.like.controller;

import kr.ac.jbnu.ksh.blogtp.like.dto.LikeCountResponse;
import kr.ac.jbnu.ksh.blogtp.like.service.LikeService;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Likes", description = "좋아요 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("${app.api-prefix:/api}/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 수 조회", description = "특정 게시글의 좋아요 수를 조회합니다.")
    @GetMapping
    public LikeCountResponse count(@PathVariable Long postId) {
        return likeService.count(postId);
    }

    @Operation(summary = "좋아요", description = "게시글에 좋아요를 추가합니다. (로그인 필요)")
    @PostMapping
    public LikeCountResponse like(@PathVariable Long postId) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return likeService.like(postId, p);
    }

    @Operation(summary = "좋아요 취소", description = "게시글 좋아요를 취소합니다. (로그인 필요)")
    @DeleteMapping
    public LikeCountResponse unlike(@PathVariable Long postId) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return likeService.unlike(postId, p);
    }
}
