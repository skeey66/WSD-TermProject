package kr.ac.jbnu.ksh.blogtp.stats.dto;

public record DailyCountResponse(
        String day,
        long count
) {}
