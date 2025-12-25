package kr.ac.jbnu.ksh.blogtp.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot usually auto-configures an {@link ObjectMapper}.
 * Some minimal/container builds may end up without it, and our RateLimitFilter
 * needs it to serialize the JSON error response.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Keep it simple and predictable for API error responses.
        return mapper;
    }
}
