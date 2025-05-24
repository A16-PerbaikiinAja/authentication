package id.ac.ui.cs.advprog.authentication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.authentication.dto.ProfileResponseDto;
import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.authentication.service.ProfileService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getProfile_requiresAuth() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user-uuid", roles = "USER")
    void getProfile_returnsUserProfile() throws Exception {
        ProfileResponseDto dto = ProfileResponseDto.builder()
                .fullName("Alice")
                .email("alice@example.com")
                .phoneNumber("123")
                .address("Home")
                .profilePhoto("alice.png")
                .build();

        when(profileService.getProfile("user-uuid", "USER")).thenReturn(dto);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("123"))
                .andExpect(jsonPath("$.address").value("Home"))
                .andExpect(jsonPath("$.profilePhoto").value("alice.png"));

        verify(profileService).getProfile("user-uuid", "USER");
    }

    @Test
    @WithMockUser(username = "tech-uuid", roles = "TECHNICIAN")
    void getProfile_returnsTechnicianProfile() throws Exception {
        ProfileResponseDto dto = ProfileResponseDto.builder()
                .fullName("Bob")
                .email("bob@example.com")
                .phoneNumber("456")
                .address("Work")
                .profilePhoto("bob.png")
                .experience(2)
                .totalJobsCompleted(3)
                .totalEarnings(300.0)
                .build();

        when(profileService.getProfile("tech-uuid", "TECHNICIAN")).thenReturn(dto);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experience").value(2))
                .andExpect(jsonPath("$.totalJobsCompleted").value(3))
                .andExpect(jsonPath("$.totalEarnings").value(300.0));

        verify(profileService).getProfile("tech-uuid", "TECHNICIAN");
    }

    @Test
    @WithMockUser(username = "admin-uuid", roles = "ADMIN")
    void getProfile_returnsAdminProfile() throws Exception {
        ProfileResponseDto dto = ProfileResponseDto.builder()
                .fullName("Charlie")
                .email("charlie@example.com")
                .phoneNumber("789")
                .build();

        when(profileService.getProfile("admin-uuid", "ADMIN")).thenReturn(dto);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("789"));

        verify(profileService).getProfile("admin-uuid", "ADMIN");
    }

    @Test
    @WithMockUser(username = "guest-uuid", roles = "GUEST")
    void getProfile_invalidRole() throws Exception {
        when(profileService.getProfile("guest-uuid", "GUEST"))
                .thenThrow(new Exception("Profile retrieval not allowed"));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Profile retrieval not allowed"));

        verify(profileService).getProfile("guest-uuid", "GUEST");
    }

    @Test
    void updateProfile_requiresAuth() throws Exception {
        mockMvc.perform(put("/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user-uuid", roles = "USER")
    void updateProfile_updatesUserProfile() throws Exception {
        ProfileUpdateDto reqDto = new ProfileUpdateDto();
        reqDto.setFullName("NewAlice");
        reqDto.setPhoneNumber("321");

        ProfileResponseDto respDto = ProfileResponseDto.builder()
                .fullName("NewAlice")
                .email("alice@example.com")
                .phoneNumber("321")
                .address("Home")
                .profilePhoto("alice.png")
                .build();

        when(profileService.updateProfile(eq(reqDto), eq("user-uuid"), eq("USER")))
                .thenReturn(respDto);

        mockMvc.perform(put("/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("NewAlice"))
                .andExpect(jsonPath("$.phoneNumber").value("321"));

        verify(profileService).updateProfile(eq(reqDto), eq("user-uuid"), eq("USER"));
    }

    @Test
    @WithMockUser(username = "user-uuid", roles = "USER")
    void updateProfile_serviceError() throws Exception {
        ProfileUpdateDto reqDto = new ProfileUpdateDto();
        reqDto.setFullName("X");

        when(profileService.updateProfile(any(ProfileUpdateDto.class), eq("user-uuid"), eq("USER")))
                .thenThrow(new Exception("Update failed"));

        mockMvc.perform(put("/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Update failed"));

        verify(profileService).updateProfile(any(ProfileUpdateDto.class), eq("user-uuid"), eq("USER"));
    }
}
