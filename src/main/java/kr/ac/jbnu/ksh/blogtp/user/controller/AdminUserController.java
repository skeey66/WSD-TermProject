package kr.ac.jbnu.ksh.blogtp.user.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.common.dto.PageResponse;
import kr.ac.jbnu.ksh.blogtp.user.dto.AdminUpdateRoleRequest;
import kr.ac.jbnu.ksh.blogtp.user.dto.AdminUpdateStatusRequest;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;
import kr.ac.jbnu.ksh.blogtp.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Admin", description = "관리자 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("${app.api-prefix:/api}/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "회원 목록 조회", description = "관리자 권한으로 회원 목록을 조회합니다.")
    @GetMapping
    public PageResponse<UserResponse> list(Pageable pageable) {
        Page<UserResponse> page = adminUserService.list(pageable);
        return PageResponse.from(page);
    }

    @Operation(summary = "회원 권한 변경", description = "관리자 권한으로 회원의 역할(Role)을 변경합니다.")
    @PatchMapping("/{userId}/role")
    public UserResponse changeRole(@PathVariable Long userId, @Valid @RequestBody AdminUpdateRoleRequest req) {
        return adminUserService.changeRole(userId, req.role());
    }

    @Operation(
            summary = "회원 상태 변경",
            description = "관리자 권한으로 회원의 상태(활성/비활성 등)를 변경합니다. 존재하지 않는 회원이면 404를 반환합니다."
    )
    @PatchMapping("/{userId}/status")
    public UserResponse changeStatus(@PathVariable Long userId, @Valid @RequestBody AdminUpdateStatusRequest req) {
        return adminUserService.changeStatus(userId, req.status());
    }
}
