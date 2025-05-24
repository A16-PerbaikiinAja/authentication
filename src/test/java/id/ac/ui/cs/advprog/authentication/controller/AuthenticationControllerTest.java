package id.ac.ui.cs.advprog.authentication.controller;

import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.ChangePasswordDto;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void loginSuccess() throws Exception {
        when(authenticationService.login(any(AuthRequest.class)))
                .thenReturn(new AuthResponse("token123"));

        AuthRequest req = new AuthRequest();
        req.setEmail("user@example.com");
        req.setPassword("Password123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    void loginValidationError() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void loginFailure() throws Exception {
        when(authenticationService.login(any(AuthRequest.class)))
                .thenThrow(new Exception("Bad credentials"));

        AuthRequest req = new AuthRequest();
        req.setEmail("user@example.com");
        req.setPassword("WrongPassword123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad credentials"));
    }

    @Test
    void registerUserSuccess() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setPassword("Password123!");
        dto.setAddress("123 Main St");

        mockMvc.perform(post("/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(isEmptyString()));

        verify(authenticationService).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void registerUserValidationError() throws Exception {
        mockMvc.perform(post("/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void registerUserDuplicateEmail() throws Exception {
        doThrow(new IllegalArgumentException("Email is already in use"))
                .when(authenticationService).registerUser(any(UserRegistrationDto.class));

        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName("Bob");
        dto.setEmail("bob@example.com");
        dto.setPhoneNumber("0987654321");
        dto.setPassword("Password123!");
        dto.setAddress("456 Elm St");

        mockMvc.perform(post("/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    @Test
    void registerTechnicianUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/register/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.experience").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerTechnicianSuccess() throws Exception {
        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setFullName("Charlie");
        dto.setEmail("charlie@example.com");
        dto.setPhoneNumber("1122334455");
        dto.setPassword("Password123!");
        dto.setAddress("789 Oak St");
        dto.setExperience(3);

        mockMvc.perform(post("/auth/register/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(isEmptyString()));

        verify(authenticationService).registerTechnician(any(TechnicianRegistrationDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerTechnicianValidationError() throws Exception {
        mockMvc.perform(post("/auth/register/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerTechnicianDuplicateEmail() throws Exception {
        doThrow(new IllegalArgumentException("Email is already in use"))
                .when(authenticationService).registerTechnician(any(TechnicianRegistrationDto.class));

        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setFullName("Dana");
        dto.setEmail("dana@example.com");
        dto.setPhoneNumber("6677889900");
        dto.setPassword("Password123!");
        dto.setAddress("321 Pine St");
        dto.setExperience(4);

        mockMvc.perform(post("/auth/register/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    @Test
    void changePasswordUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.oldPassword").exists())
                .andExpect(jsonPath("$.newPassword").exists());
    }

    @Test
    @WithMockUser(username = "user-123")
    void changePasswordSuccess() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("OldPassword1!");
        dto.setNewPassword("NewValidPass1!");

        mockMvc.perform(post("/auth/change-password")
                        .principal((Principal) () -> "user-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(isEmptyString()));

        verify(authenticationService)
                .changePassword("user-123", "OldPassword1!", "NewValidPass1!");
    }

    @Test
    @WithMockUser
    void changePasswordValidationError() throws Exception {
        mockMvc.perform(post("/auth/change-password")
                        .principal((Principal) () -> "user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.oldPassword").exists())
                .andExpect(jsonPath("$.newPassword").exists());
    }

    @Test
    @WithMockUser(username = "user-123")
    void changePasswordFailure() throws Exception {
        doThrow(new IllegalArgumentException("Old password is incorrect"))
                .when(authenticationService).changePassword("user-123", "WrongOldPass1!", "NewPassword123!");

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("WrongOldPass1!");
        dto.setNewPassword("NewPassword123!");

        mockMvc.perform(post("/auth/change-password")
                        .principal((Principal) () -> "user-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Old password is incorrect"));
    }
}
