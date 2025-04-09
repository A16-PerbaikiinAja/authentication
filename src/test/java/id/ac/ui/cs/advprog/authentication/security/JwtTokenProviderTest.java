package id.ac.ui.cs.advprog.authentication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "qqFQ/tEeaSlcEwUtG+l30VGNjd+z2BfA5Y5QWsiZWHEFhDgB6kw2YODG9f6cIGn44/DtjmZNxUPH97+YDpR/Ng==";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
    }

    @Test
    void testGenerateAndParseToken() {
        String email = "test@example.com";
        String role = "ADMIN";
        String token = jwtTokenProvider.generateToken(email, role);
        assertNotNull(token, "Token should not be null");
        assertEquals(email, jwtTokenProvider.getEmailFromJWT(token), "Parsed email should match");
        assertEquals(role, jwtTokenProvider.getRoleFromJWT(token), "Parsed role should match");
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtTokenProvider.generateToken("user@example.com", "USER");
        assertTrue(jwtTokenProvider.validateToken(token), "Token should be valid");
    }

    @Test
    void testValidateToken_Invalid() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtTokenProvider.validateToken(invalidToken), "Token should be invalid");
    }
}
