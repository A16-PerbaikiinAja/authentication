package id.ac.ui.cs.advprog.authentication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.authentication.service.ProfileService;

@WebMvcTest(ProfileController.class)
@Import(id.ac.ui.cs.advprog.authentication.config.SecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000003", roles = {"USER"})
    void testUpdateProfile_UserSuccess() throws Exception {
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("Updated User");
        dto.setPhoneNumber("+1112223333");
        dto.setAddress("Updated Address");
        dto.setProfilePhoto("updated-user.png");
        dto.setPassword("newPassword");

        id.ac.ui.cs.advprog.authentication.model.User updatedUser =
                new id.ac.ui.cs.advprog.authentication.model.User("Updated User", "user@example.com", "+1112223333", "hashedNewPassword", "Updated Address");
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        updatedUser.setId(userId);
        updatedUser.setProfilePhoto("updated-user.png");

        Mockito.when(profileService.updateProfile(any(ProfileUpdateDto.class), eq("00000000-0000-0000-0000-000000000003"), eq("USER")))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.fullName").value("Updated User"))
                .andExpect(jsonPath("$.profilePhoto").value("updated-user.png"));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000004", roles = {"TECHNICIAN"})
    void testUpdateProfile_TechnicianSuccess() throws Exception {
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("Updated Tech");
        dto.setPhoneNumber("9998887777");
        dto.setAddress("Updated Tech Address");
        dto.setProfilePhoto("updated-tech.png");
        dto.setPassword("newTechPassword");
        dto.setExperience(7);

        id.ac.ui.cs.advprog.authentication.model.Technician updatedTech =
                new id.ac.ui.cs.advprog.authentication.model.Technician("Updated Tech", "tech@example.com", "9998887777", "hashedTechPassword", 7, "Updated Tech Address", 0, 0.0);
        UUID techId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        updatedTech.setId(techId);
        updatedTech.setProfilePhoto("updated-tech.png");

        Mockito.when(profileService.updateProfile(any(ProfileUpdateDto.class), eq("00000000-0000-0000-0000-000000000004"), eq("TECHNICIAN")))
                .thenReturn(updatedTech);

        mockMvc.perform(put("/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(techId.toString()))
                .andExpect(jsonPath("$.fullName").value("Updated Tech"))
                .andExpect(jsonPath("$.profilePhoto").value("updated-tech.png"));
    }
}
