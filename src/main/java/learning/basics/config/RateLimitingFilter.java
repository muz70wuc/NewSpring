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
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("text/html; charset=UTF-8");
    
                String htmlResponse = """
                    <!DOCTYPE html>
                    <html lang="de">
                    <head>
                        <meta charset="UTF-8">
                        <title>Zu viele Anfragen</title>
                        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
                    </head>
                    <body>
                        <main class="container" style="max-width: 500px; margin-top: 5rem;">
                            <article>
                                <h2>Zu viele Versuche</h2>
                                <p>Du hast die maximale Anzahl an Versuchen erreicht. Bitte warte 5 Minute, bevor du es erneut versuchst.</p>
                                <a href="/register" role="button">Zurück zur Registrierung</a>
                            </article>
                        </main>
                    </body>
                    </html>
                    """;
        
                response.getWriter().write(htmlResponse);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
