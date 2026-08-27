package learning.basics.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Maximal 3 Anfragen pro 5 Minuten
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillIntervally(3, Duration.ofMinutes(5))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Nur den POST auf /register limitieren
        if ("/register".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            
            // Liest bei Cloudflare die echte Client-IP aus
            String clientIp = request.getHeader("CF-Connecting-IP");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }

            Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // HTTP 429
                response.setContentType("text/plain; charset=UTF-8");     // Verhindert den Download!
                response.getWriter().write("Zu viele Registrierungsversuche. Bitte warte 1 Minute.");
                return; // Bricht die Filterkette ab
            }
        }

        filterChain.doFilter(request, response);
    }
}
