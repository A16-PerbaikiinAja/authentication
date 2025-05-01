package id.ac.ui.cs.advprog.authentication.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.authentication.service.ProfileService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@Import(id.ac.ui.cs.advprog.authentication.config.SecurityConfig.class)
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired private ObjectMapper mapper;
    @MockBean private ProfileService svc;

    @BeforeEach
    void setupFilter() throws Exception {
        doAnswer(invocation -> {
            var req   = invocation.getArgument(0, HttpServletRequest.class);
            var resp  = invocation.getArgument(1, HttpServletResponse.class);
            var chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(req, resp);
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(HttpServletRequest.class),
                        any(HttpServletResponse.class),
                        any(FilterChain.class));
    }

    private static final String USER_ID = "00000000-0000-0000-0000-000000000003";
    private static final String TECH_ID = "00000000-0000-0000-0000-000000000004";
    private static final String ADMIN_ID = "00000000-0000-0000-0000-000000000005";

    @Nested class GetProfile {
        @Test @WithMockUser(username=USER_ID, roles="USER")
        void userSuccess() throws Exception {
            var u = new User("Alice","a@x.com","+1","pwd","addr");
            u.setId(UUID.fromString(USER_ID));
            given(svc.getProfile(USER_ID, "USER")).willReturn(u);

            var res = mvc.perform(get("/profile")).andExpect(status().isOk()).andReturn();
            var body = mapper.readValue(res.getResponse().getContentAsString(), new TypeReference<Map<String,Object>>() {});
            assertThat(body).containsEntry("id", USER_ID).containsEntry("role","USER");
        }

        @Test @WithMockUser(username=TECH_ID, roles="TECHNICIAN")
        void techSuccess() throws Exception {
            var t = new Technician("Bob","b@x.com","222","pwd",5,"addr",0,0.0);
            t.setId(UUID.fromString(TECH_ID));
            given(svc.getProfile(TECH_ID, "TECHNICIAN")).willReturn(t);

            mvc.perform(get("/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TECH_ID))
                    .andExpect(jsonPath("$.role").value("TECHNICIAN"));
        }

        @Test @WithMockUser(username = ADMIN_ID, roles = "ADMIN")
        void adminSuccess() throws Exception {
            var a = new Admin("Carol", "c@x.com", "333", "pwd");
            a.setId(UUID.fromString(ADMIN_ID));
            given(svc.getProfile(ADMIN_ID, "ADMIN")).willReturn(a);

            mvc.perform(get("/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ADMIN_ID))
                    .andExpect(jsonPath("$.role").value("ADMIN"));
        }

        @Test @WithMockUser(username=USER_ID, roles="USER")
        void errorPath() throws Exception {
            given(svc.getProfile(USER_ID, "USER")).willThrow(new RuntimeException("Not found"));
            mvc.perform(get("/profile"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Not found"));
        }
    }

    @Nested class UpdateProfile {
        @Test @WithMockUser(username=USER_ID, roles="USER")
        void userSuccess() throws Exception {
            var dto = new ProfileUpdateDto();
            dto.setFullName("Alice2");
            var updated = new User("Alice2","a@x.com","+1","hashed","addr");
            updated.setId(UUID.fromString(USER_ID));
            given(svc.updateProfile(dto, USER_ID, "USER")).willReturn(updated);

            mvc.perform(put("/profile").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Alice2"));
        }

        @Test @WithMockUser(username=USER_ID, roles="USER")
        void serviceError() throws Exception {
            var dto = new ProfileUpdateDto();
            given(svc.updateProfile(dto, USER_ID, "USER")).willThrow(new IllegalArgumentException("Bad"));
            mvc.perform(put("/profile").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Bad"));
        }
    }
}
