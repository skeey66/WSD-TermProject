package kr.ac.jbnu.ksh.blogtp.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorResponse;
import kr.ac.jbnu.ksh.blogtp.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "app.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${app.rate-limit.per-minute}")
    private long limitPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = buildKey(request);
        String redisKey = "rl:" + key + ":" + currentMinuteBucket();

        Long cnt = redis.opsForValue().increment(redisKey);
        if (cnt != null && cnt == 1L) {
            redis.expire(redisKey, Duration.ofMinutes(2));
        }

        if (cnt != null && cnt > limitPerMinute) {
            response.setStatus(ErrorCode.TOO_MANY_REQUESTS.getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    ErrorResponse.of(request.getRequestURI(), ErrorCode.TOO_MANY_REQUESTS, null, null));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String buildKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof AuthPrincipal p) {
            return "u:" + p.userId();
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }
        return "ip:" + ip;
    }

    private String currentMinuteBucket() {
        long minutes = System.currentTimeMillis() / 1000L / 60L;
        return String.valueOf(minutes);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/health")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator")
                || uri.startsWith("/favicon");
    }
}
