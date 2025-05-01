package id.ac.ui.cs.advprog.authentication.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private HttpServletRequest   request;
    @Mock private HttpServletResponse  response;
    @Mock private FilterChain          filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        verifyNoInteractions(response);
    }

    private void doFilter() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);
        then(filterChain).should().doFilter(request, response);
    }

    @Nested
    class HeaderScenarios {
        @Test
        void whenAuthorizationHeaderHasValidBearerToken_thenAuthenticationSet() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer abc123");
            given(jwtTokenProvider.validateToken("abc123")).willReturn(true);
            given(jwtTokenProvider.getUserIdFromJWT("abc123")).willReturn("uid-1");
            given(jwtTokenProvider.getRoleFromJWT("abc123")).willReturn("user");

            doFilter();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("uid-1");
            assertThat(auth.getAuthorities())
                    .extracting("authority")
                    .containsExactly("ROLE_USER");
        }

        @ParameterizedTest
        @ValueSource(strings = {"WrongPrefix token", ""})
        void whenHeaderMalformed_noValidateAndNoAuth(String headerValue) throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(headerValue);
            given(request.getCookies()).willReturn(null);

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            then(jwtTokenProvider).should(never()).validateToken(any());
        }

        @Test
        void whenBearerWithNoToken_validateCalledWithEmptyStringAndNoAuth() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer ");

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            then(jwtTokenProvider).should().validateToken("");
        }

        @Test
        void whenTokenValidationFails_noAuth() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer bad");
            given(jwtTokenProvider.validateToken("bad")).willReturn(false);

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            then(jwtTokenProvider).should().validateToken("bad");
        }

        @Test
        void whenValidateThrowsException_swallowedAndContinues() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer boom");
            given(jwtTokenProvider.validateToken("boom")).willThrow(new JwtException("explode"));

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Nested
    class CookieScenarios {
        @Test
        void whenCookieContainsToken_thenAuthenticationSet() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);
            given(request.getCookies())
                    .willReturn(new Cookie[]{
                            new Cookie("other","x"),
                            new Cookie("token","cookietoken"),
                            new Cookie("foo","bar")
                    });

            given(jwtTokenProvider.validateToken("cookietoken")).willReturn(true);
            given(jwtTokenProvider.getUserIdFromJWT("cookietoken")).willReturn("uid-2");
            given(jwtTokenProvider.getRoleFromJWT("cookietoken")).willReturn("ADMIN");

            doFilter();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("uid-2");
            assertThat(auth.getAuthorities())
                    .extracting("authority")
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        void whenNoTokenCookie_noAuth() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);
            given(request.getCookies()).willReturn(new Cookie[]{ new Cookie("x","1") });

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void whenCookiesNull_noNpeAndNoAuth() throws Exception {
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);
            given(request.getCookies()).willReturn(null);

            doFilter();

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}
