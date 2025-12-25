package kr.ac.jbnu.ksh.blogtp.stats.service;

import kr.ac.jbnu.ksh.blogtp.stats.dto.DailyCountResponse;
import kr.ac.jbnu.ksh.blogtp.stats.dto.TopAuthorResponse;
import kr.ac.jbnu.ksh.blogtp.stats.dto.DailyCount;
import kr.ac.jbnu.ksh.blogtp.stats.dto.TopAuthor;
import kr.ac.jbnu.ksh.blogtp.stats.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    @Transactional(readOnly = true)
    public List<DailyCountResponse> dailyPosts(int days) {
        return statsRepository.dailyPosts(days).stream()
                .map(DailyCount::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopAuthorResponse> topAuthors(int days, int limit) {
        return statsRepository.topAuthors(days, limit).stream()
                .map(TopAuthor::toResponse)
                .toList();
    }
}
