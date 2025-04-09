package id.ac.ui.cs.advprog.authentication.security;

import java.security.Key;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String jwtSecret;
    private final long JWT_EXPIRATION_MS = 3600000; // 1 hour

    public JwtTokenProvider() {
    }

    private Key getSigningKey() {
    }

    public String generateToken(String email, String role) {
    }

    public String getEmailFromJWT(String token) {
    }

    public String getRoleFromJWT(String token) {
    }

    public boolean validateToken(String authToken) {
    }
}
