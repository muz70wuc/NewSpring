package learning.basics.config;

import static org.mockito.Mockito.mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import learning.basics.service.CustomUserDetailsService;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return mock(CustomUserDetailsService.class);
    }
}