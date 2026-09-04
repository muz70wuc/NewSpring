package learning.basics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import learning.basics.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final RateLimitingFilter rateLimitingFilter;

    // CustomUserDetailsService per Konstruktor einbinden
    public SecurityConfig(CustomUserDetailsService userDetailsService, RateLimitingFilter rateLimitingFilter) {
        this.userDetailsService = userDetailsService;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/terms", "/privacy", "/register", "/login", "/h2-console/**", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/userHome", true)
                        .permitAll())
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                );

        // Verhindert, dass der Browser geschützte Seiten (wie /userHome) im lokalen Cache speichert
        http.headers(headers -> headers
            .cacheControl(cache -> cache.disable()) 
            .frameOptions(frame -> frame.sameOrigin())
        );

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.addFilterBefore(rateLimitingFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}