package kr.ac.jbnu.ksh.blogtp.system;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Health", description = "헬스체크 API")
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final Environment environment;

    @Value("${spring.application.name:blogtp}")
    private String appName;

    @Value("${app.api-prefix:/api}")
    private String apiPrefix;

    @Value("${app.jwt.issuer:blogtp}")
    private String issuer;

    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", "UP");
        m.put("time", OffsetDateTime.now().toString());
        m.put("app", appName);
        m.put("apiPrefix", apiPrefix);
        m.put("issuer", issuer);
        m.put("profiles", String.join(",", environment.getActiveProfiles()));
        return m;
    }
}
