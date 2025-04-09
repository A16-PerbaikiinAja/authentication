package id.ac.ui.cs.advprog.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    }
}
