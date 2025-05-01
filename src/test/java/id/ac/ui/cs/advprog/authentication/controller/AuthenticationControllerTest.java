package id.ac.ui.cs.advprog.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authentication.config.SecurityConfig;
import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "debug=false")
class AuthenticationControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private AuthenticationService authService;
    @MockBean private JwtAuthenticationFilter jwtFilter;

    @BeforeEach
    void bypassFilter() throws Exception {
        doAnswer(invocation -> {
            HttpServletRequest req   = invocation.getArgument(0);
            HttpServletResponse res   = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(req, res);
            return null;
        })
                .when(jwtFilter)
                .doFilter(
                        any(HttpServletRequest.class),
                        any(HttpServletResponse.class),
                        any(FilterChain.class)
                );
    }

    private static class AuthRequestBuilder {
        private String email = "user@example.com";
        private String password = "Password1!";
        AuthRequestBuilder email(String e) { this.email = e; return this; }
        AuthRequestBuilder password(String p) { this.password = p; return this; }
        AuthRequest build() {
            var r = new AuthRequest();
            r.setEmail(email);
            r.setPassword(password);
            return r;
        }
    }

    @Nested class LoginTests {
        @ParameterizedTest
        @MethodSource("goodCredentials")
        @WithAnonymousUser
        void loginSuccess(String email, String pwd) throws Exception {
            var req = new AuthRequestBuilder().email(email).password(pwd).build();
            given(authService.login(req)).willReturn(new AuthResponse("token-"+email));

            var result = mvc.perform(post("/auth/login").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("token=token-"+email)))
                    .andReturn();

            verify(authService).login(req);
            String cookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
            assertThat(cookie)
                    .contains("HttpOnly")
                    .contains("SameSite=Strict")
                    .contains("Secure");
        }

        static Stream<String[]> goodCredentials() {
            return Stream.of(
                    new String[]{"admin@x.com","Admin123!"},
                    new String[]{"tech@x.com","Tech123!"},
                    new String[]{"user@x.com","User123!"}
            );
        }

        @Test @WithAnonymousUser
        void loginValidationFails() throws Exception {
            var bad = new AuthRequestBuilder().email("").password("short").build();
            mvc.perform(post("/auth/login").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.password").exists());
        }

        @Test @WithAnonymousUser
        void loginServiceThrows() throws Exception {
            var req = new AuthRequestBuilder().build();
            given(authService.login(req)).willThrow(new RuntimeException("Auth failed"));

            mvc.perform(post("/auth/login").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Auth failed"));
        }
    }

    @Nested class LogoutTests {
        @Test @WithAnonymousUser
        void logoutClearsCookie() throws Exception {
            mvc.perform(post("/auth/logout").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("token=")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("Max-Age=0")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("HttpOnly")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("SameSite=Strict")));
        }
    }

    @Nested class RegisterUserTests {
        @Test @WithAnonymousUser
        void success() throws Exception {
            var dto = new UserRegistrationDto();
            dto.setFullName("John");
            dto.setEmail("john@x.com");
            dto.setPhoneNumber("+1234567890");
            dto.setPassword("Password1!");
            dto.setAddress("Addr");

            var u = new User("John", dto.getEmail(), dto.getPhoneNumber(), dto.getPassword(), dto.getAddress());
            u.setId(UUID.randomUUID());
            given(authService.registerUser(any())).willReturn(u);

            mvc.perform(post("/auth/register/user").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(u.getId().toString()));
        }

        @Test @WithAnonymousUser
        void validationFails() throws Exception {
            mvc.perform(post("/auth/register/user").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fullName").exists())
                    .andExpect(jsonPath("$.email").exists());
        }

        @Test @WithAnonymousUser
        void serviceThrows() throws Exception {
            var dto = new UserRegistrationDto();
            dto.setFullName("Jane");
            dto.setEmail("jane@x.com");
            dto.setPhoneNumber("+1234567890");
            dto.setPassword("Password1!");
            dto.setAddress("Addr");

            given(authService.registerUser(dto))
                    .willThrow(new IllegalArgumentException("Email is already in use"));

            mvc.perform(post("/auth/register/user").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Email is already in use"));
        }
    }

    @Nested class RegisterTechTests {
        @Test @WithMockUser(roles="ADMIN")
        void success() throws Exception {
            var dto = new TechnicianRegistrationDto();
            dto.setFullName("Tech");
            dto.setEmail("tech@x.com");
            dto.setPhoneNumber("+1234567890");
            dto.setPassword("Password1!");
            dto.setExperience(2);
            dto.setAddress("Addr");

            var t = new Technician(
                    dto.getFullName(),
                    dto.getEmail(),
                    dto.getPhoneNumber(),
                    dto.getPassword(),
                    dto.getExperience(),
                    dto.getAddress(),
                    0, 0.0
            );
            t.setId(UUID.randomUUID());
            given(authService.registerTechnician(any())).willReturn(t);

            mvc.perform(post("/auth/register/technician").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(t.getId().toString()));
        }

        @Test @WithMockUser(roles = "ADMIN")
        void serviceThrows() throws Exception {
            var dto = new TechnicianRegistrationDto();
            dto.setFullName("Tech");
            dto.setEmail("dup@x.com");
            dto.setPhoneNumber("+1234567890");
            dto.setPassword("Password1!");
            dto.setExperience(2);
            dto.setAddress("Addr");

            given(authService.registerTechnician(any(TechnicianRegistrationDto.class)))
                    .willThrow(new IllegalArgumentException("Email is already in use"));

            mvc.perform(post("/auth/register/technician")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Email is already in use"));
        }

        @Test @WithMockUser(roles="USER")
        void forbiddenForNonAdmin() throws Exception {
            mvc.perform(post("/auth/register/technician").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }
}
