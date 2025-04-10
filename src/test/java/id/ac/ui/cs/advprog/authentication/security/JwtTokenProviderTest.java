package id.ac.ui.cs.advprog.authentication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testUserId = "00000000-0000-0000-0000-000000000001";
    private final String testSecret = "qqFQ/tEeaSlcEwUtG+l30VGNjd+z2BfA5Y5QWsiZWHEFhDgB6kw2YODG9f6cIGn44/DtjmZNxUPH97+YDpR/Ng==";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
    }

    @Test
    void testGenerateAndParseToken() {
        String role = "ADMIN";
        String token = jwtTokenProvider.generateToken(testUserId, role);
        assertNotNull(token, "Token should not be null");
        assertEquals(testUserId, jwtTokenProvider.getUserIdFromJWT(token), "Parsed user id should match");
        assertEquals(role, jwtTokenProvider.getRoleFromJWT(token), "Parsed role should match");
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtTokenProvider.generateToken("00000000-0000-0000-0000-000000000002", "USER");
        assertTrue(jwtTokenProvider.validateToken(token), "Token should be valid");
    }

    @Test
    void testValidateToken_Invalid() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtTokenProvider.validateToken(invalidToken), "Token should be invalid");
    }
}
