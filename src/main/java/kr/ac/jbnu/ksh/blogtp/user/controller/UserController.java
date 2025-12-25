package kr.ac.jbnu.ksh.blogtp.user.controller;

import jakarta.validation.Valid;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import kr.ac.jbnu.ksh.blogtp.security.SecurityUtils;
import kr.ac.jbnu.ksh.blogtp.user.dto.UpdatePasswordRequest;
import kr.ac.jbnu.ksh.blogtp.user.dto.UserResponse;
import kr.ac.jbnu.ksh.blogtp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Users", description = "회원 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("${app.api-prefix:/api}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public UserResponse me() {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return userService.me(p);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public UserResponse changePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        return userService.changePassword(p, req);
    }

    @Operation(summary = "회원 비활성화", description = "현재 계정을 비활성화(탈퇴)합니다.")
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate() {
        AuthPrincipal p = SecurityUtils.currentPrincipalOrThrow();
        userService.deactivate(p);
    }
}
