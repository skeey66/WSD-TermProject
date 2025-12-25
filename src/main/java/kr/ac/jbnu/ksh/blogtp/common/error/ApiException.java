package kr.ac.jbnu.ksh.blogtp.common.error;

import lombok.Getter;

import java.util.Map;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String messageOverride;
    private final Map<String, Object> details;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ApiException(ErrorCode errorCode, String messageOverride) {
        this(errorCode, messageOverride, null);
    }

    public ApiException(ErrorCode errorCode, String messageOverride, Map<String, Object> details) {
        super(messageOverride != null ? messageOverride : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.messageOverride = messageOverride;
        this.details = details;
    }
}
