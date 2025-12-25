package kr.ac.jbnu.ksh.blogtp.security;

public record AuthPrincipal(Long userId, String email, String role) {}
