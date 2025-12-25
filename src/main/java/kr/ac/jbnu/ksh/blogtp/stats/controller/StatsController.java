package kr.ac.jbnu.ksh.blogtp.stats.controller;

import kr.ac.jbnu.ksh.blogtp.stats.dto.DailyCountResponse;
import kr.ac.jbnu.ksh.blogtp.stats.dto.TopAuthorResponse;
import kr.ac.jbnu.ksh.blogtp.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Stats", description = "통계 API")
@RestController
@RequestMapping("${app.api-prefix:/api}/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "일자별 게시글 수", description = "최근 N일간 일자별 게시글 작성 수를 조회합니다.")
    @GetMapping("/daily-posts")
    public List<DailyCountResponse> dailyPosts(@RequestParam(defaultValue = "7") int days) {
        return statsService.dailyPosts(days);
    }

    @Operation(summary = "상위 작성자", description = "게시글 작성 수 기준 상위 작성자를 조회합니다.")
    @GetMapping("/top-authors")
    public List<TopAuthorResponse> topAuthors(@RequestParam(defaultValue = "30") int days,
                                             @RequestParam(defaultValue = "5") int limit) {
        return statsService.topAuthors(days, limit);
    }
}
