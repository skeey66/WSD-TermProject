package kr.ac.jbnu.ksh.blogtp.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        String timestamp,
        String path,
        int status,
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, Object> details
) {
    public static ErrorResponse of(String path, ErrorCode errorCode, String message, Map<String, Object> details) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .path(path)
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(message != null ? message : errorCode.getDefaultMessage())
                .details(details)
                .build();
    }
}
