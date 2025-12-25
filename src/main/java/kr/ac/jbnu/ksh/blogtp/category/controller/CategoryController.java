package kr.ac.jbnu.ksh.blogtp.category.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.category.dto.CategoryRequest;
import kr.ac.jbnu.ksh.blogtp.category.dto.CategoryResponse;
import kr.ac.jbnu.ksh.blogtp.category.service.CategoryService;
import kr.ac.jbnu.ksh.blogtp.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Categories", description = "카테고리 API")
@RestController
@RequestMapping("${app.api-prefix:/api}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록 조회", description = "카테고리 목록을 조회합니다.")
    @GetMapping
    public PageResponse<CategoryResponse> list(Pageable pageable) {
        Page<CategoryResponse> page = categoryService.list(pageable);
        return PageResponse.from(page);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 생성", description = "새 카테고리를 생성합니다. (관리자 권한)")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest req) {
        return categoryService.create(req);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 수정", description = "기존 카테고리를 수정합니다. (관리자 권한)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        return categoryService.update(id, req);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다. (관리자 권한)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
