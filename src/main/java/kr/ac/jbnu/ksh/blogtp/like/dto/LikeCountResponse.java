package kr.ac.jbnu.ksh.blogtp.like.dto;

public record LikeCountResponse(
        Long postId,
        long likes
) {}
