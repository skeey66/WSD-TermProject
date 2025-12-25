package kr.ac.jbnu.ksh.blogtp.bookmark.controller;

import kr.ac.jbnu.ksh.blogtp.bookmark.dto.BookmarkResponse;
import kr.ac.jbnu.ksh.blogtp.bookmark.service.BookmarkService;
import kr.ac.jbnu.ksh.blogtp.common.dto.PageResponse;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Bookmarks", description = "북마크 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 추가", description = "게시글을 북마크에 추가합니다. (로그인 필요)")
    @PostMapping("${app.api-prefix:/api}/posts/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long postId) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        bookmarkService.add(postId, p);
    }

    @Operation(summary = "북마크 삭제", description = "게시글을 북마크에서 제거합니다. (로그인 필요)")
    @DeleteMapping("${app.api-prefix:/api}/posts/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long postId) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        bookmarkService.remove(postId, p);
    }

    @GetMapping("${app.api-prefix:/api}/bookmarks/me")
    @Operation(
            summary = "내 북마크 목록",
            description = "내가 북마크한 게시글 목록을 페이지네이션하여 조회합니다. 기본 정렬은 최신순(createdAt desc)입니다."
    )
    public PageResponse<BookmarkResponse> my(@PageableDefault(sort = "createdAt") Pageable pageable) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return PageResponse.from(bookmarkService.myBookmarks(p, pageable));
    }
}
