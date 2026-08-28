package learning.basics.securityTests;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import learning.basics.config.RateLimitingFilter;

class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Andere Pfade/Methoden - Sollten ungefiltert durchgelassen werden")
    void testIgnoredRoutes() throws Exception {
        request.setRequestURI("/login");
        request.setMethod("POST");

        rateLimitingFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Erlaubte Versuche (<=3) - Sollten durchgelassen werden")
    void testAllowedRequestsUnderLimit() throws Exception {
        request.setRequestURI("/register");
        request.setMethod("POST");
        request.setRemoteAddr("192.168.1.10");

        for (int i = 0; i < 3; i++) {
            MockHttpServletResponse currentResponse = new MockHttpServletResponse();
            rateLimitingFilter.doFilter(request, currentResponse, filterChain);
            assertThat(currentResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        // any() nutzen, da im Loop unterschiedliche Response-Objekte übergeben wurden
        verify(filterChain, times(3)).doFilter(any(), any());
    }

    @Test
    @DisplayName("Limit überschritten (>3 Anfragen) - Sollte 429 Too Many Requests zurückgeben")
    void testExceedingRateLimit() throws Exception {
        request.setRequestURI("/register");
        request.setMethod("POST");
        request.setRemoteAddr("10.0.0.1");

        // 3 erlaubte Anfragen verbrauchen
        for (int i = 0; i < 3; i++) {
            rateLimitingFilter.doFilter(request, new MockHttpServletResponse(), filterChain);
        }

        // 4. Anfrage muss blockieren
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimitingFilter.doFilter(request, blockedResponse, filterChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(blockedResponse.getContentAsString()).contains("Zu viele Versuche");

        // Stellt sicher, dass die FilterChain EXAKT 3-mal aufgerufen wurde (die 4. Anfrage wurde gestoppt)
        verify(filterChain, times(3)).doFilter(any(), any());
    }

    @Test
    @DisplayName("IP-Bestimmung - CF-Connecting-IP Header bevorzugen")
    void testCloudflareIpExtraction() throws Exception {
        request.setRequestURI("/register");
        request.setMethod("POST");
        request.addHeader("CF-Connecting-IP", "203.0.113.195");
        request.addHeader("X-Forwarded-For", "198.51.100.17");

        for (int i = 0; i < 3; i++) {
            rateLimitingFilter.doFilter(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletRequest requestWithDifferentCfIp = new MockHttpServletRequest();
        requestWithDifferentCfIp.setRequestURI("/register");
        requestWithDifferentCfIp.setMethod("POST");
        requestWithDifferentCfIp.addHeader("CF-Connecting-IP", "203.0.113.196");
        requestWithDifferentCfIp.addHeader("X-Forwarded-For", "198.51.100.17");

        MockHttpServletResponse cfResponse = new MockHttpServletResponse();
        rateLimitingFilter.doFilter(requestWithDifferentCfIp, cfResponse, filterChain);

        assertThat(cfResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(filterChain, times(4)).doFilter(any(), any());
    }

    @Test
    @DisplayName("IP-Bestimmung - Erstes Element aus X-Forwarded-For parsen")
    void testXForwardedForMultipleIps() throws Exception {
        request.setRequestURI("/register");
        request.setMethod("POST");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18, 150.172.238.178");

        for (int i = 0; i < 3; i++) {
            rateLimitingFilter.doFilter(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimitingFilter.doFilter(request, blockedResponse, filterChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(filterChain, times(3)).doFilter(any(), any());
    }
}