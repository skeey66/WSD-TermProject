package kr.ac.jbnu.ksh.blogtp.stats.dto;

public record TopAuthorResponse(
        String email,
        long posts
) {}
