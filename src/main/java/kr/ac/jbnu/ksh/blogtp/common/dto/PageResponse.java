package kr.ac.jbnu.ksh.blogtp.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String sort
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        String sort = page.getSort().isSorted() ? page.getSort().toString() : "unsorted";
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                sort
        );
    }
}
