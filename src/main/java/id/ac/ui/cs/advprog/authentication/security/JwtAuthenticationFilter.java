package id.ac.ui.cs.advprog.authentication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtTokenProvider.validateToken(token)) {
                    // Token is valid, parse out email and role
                    String email = jwtTokenProvider.getEmailFromJWT(token);
                    String role = jwtTokenProvider.getRoleFromJWT(token);

                    // Convert the role claim (e.g. "ADMIN") into a Spring authority "ROLE_ADMIN"
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                    );

                    // Create an Authentication object and set it in the SecurityContext
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                // Token was invalid, expired, tampered, etc.
            }
        }

        filterChain.doFilter(request, response);
    }
}
