package kr.ac.jbnu.ksh.blogtp.stats.dto;

import java.time.LocalDate;

import kr.ac.jbnu.ksh.blogtp.stats.dto.DailyCountResponse;

/**
 * Spring Data projection for native stats queries.
 *
 * <p>Must match the column aliases in {@code StatsRepository} (date, count).
 */
public interface DailyCount {
    LocalDate getDate();

    Long getCount();

    /** Convenience mapper used by {@code StatsService}. */
    default DailyCountResponse toResponse() {
        return new DailyCountResponse(
                String.valueOf(getDate()),
                getCount() == null ? 0L : getCount()
        );
    }
}
