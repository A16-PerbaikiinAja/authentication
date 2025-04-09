package id.ac.ui.cs.advprog.authentication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.authentication.config.SecurityConfig;
import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    @WithAnonymousUser
    void testLoginSuccess() throws Exception {
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser() throws Exception {
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testRegisterTechnician_Success() throws Exception {
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testRegisterTechnician_ForbiddenForNonAdmin() throws Exception {
    }
}
