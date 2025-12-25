package kr.ac.jbnu.ksh.blogtp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Object ex = request.getAttribute("AUTH_EXCEPTION");
        ErrorCode code = (ex instanceof ApiException ae) ? ae.getErrorCode() : ErrorCode.UNAUTHORIZED;

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(request.getRequestURI(), code, null, null));
    }
}
