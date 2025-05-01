package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ProfileServiceImplTest {

    private UserRepository userRepo;
    private TechnicianRepository techRepo;
    private AdminRepository adminRepo;
    private ProfileServiceImpl svc;

    @BeforeEach
    void init() {
        userRepo = mock(UserRepository.class);
        techRepo  = mock(TechnicianRepository.class);
        adminRepo = mock(AdminRepository.class);
        svc = new ProfileServiceImpl(userRepo, techRepo, adminRepo);
    }

    @Nested class UpdateTests {
        @Test void partialUserUpdateKeepsOthers() throws Exception {
            var id = UUID.randomUUID();
            var u = new User("Name","e@x.com","+","old","addr");
            u.setId(id);
            given(userRepo.findById(id)).willReturn(Optional.of(u));
            given(userRepo.save(any(User.class))).willAnswer(i -> i.getArgument(0));

            var dto = new ProfileUpdateDto();
            dto.setAddress("newAddr");  // only address

            var updated = (User) svc.updateProfile(dto, id.toString(), "USER");
            assertThat(updated.getFullName()).isEqualTo("Name");
            assertThat(updated.getAddress()).isEqualTo("newAddr");
        }

        @Test void nullExperienceUnchanged() throws Exception {
            var id = UUID.randomUUID();
            var t = new Technician("T","t@x.com","p","old",5,"addr",0,0.0);
            t.setId(id);
            given(techRepo.findById(id)).willReturn(Optional.of(t));
            given(techRepo.save(any(Technician.class))).willAnswer(i -> i.getArgument(0));

            var dto = new ProfileUpdateDto();
            dto.setFullName("NewName");

            var updated = (Technician) svc.updateProfile(dto, id.toString(), "TECHNICIAN");
            assertThat(updated.getExperience()).isEqualTo(5);
        }

        @Test void adminUpdateNotAllowed() {
            var dto = new ProfileUpdateDto();
            assertThatThrownBy(() -> svc.updateProfile(dto, UUID.randomUUID().toString(), "ADMIN"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Profile update not allowed for role: ADMIN");
        }
    }

    @Nested class GetTests {
        @Test void getUserSuccess() throws Exception {
            var id = UUID.randomUUID();
            var u = new User("U","u@x.com","p","pwd","addr");
            u.setId(id);
            given(userRepo.findById(id)).willReturn(Optional.of(u));

            assertThat(svc.getProfile(id.toString(), "USER")).isSameAs(u);
        }

        @Test void getTechSuccess() throws Exception {
            var id = UUID.randomUUID();
            var t = new Technician("T","t@x.com","p","pwd",0,"addr",0,0.0);
            t.setId(id);
            given(techRepo.findById(id)).willReturn(Optional.of(t));

            assertThat(svc.getProfile(id.toString(), "TECHNICIAN")).isSameAs(t);
        }

        @Test
        void getAdminSuccess() throws Exception {
            var id = UUID.randomUUID();
            var a = new Admin("A", "a@x.com", "p", "pwd");
            a.setId(id);
            given(adminRepo.findById(id)).willReturn(Optional.of(a));

            assertThat(svc.getProfile(id.toString(), "ADMIN")).isSameAs(a);
        }

        @Test void adminNotFound() {
            var id = UUID.randomUUID();
            given(adminRepo.findById(id)).willReturn(Optional.empty());
            assertThatThrownBy(() -> svc.getProfile(id.toString(), "ADMIN"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Admin not found");
        }

        @Test void unsupportedRole() {
            assertThatThrownBy(() -> svc.getProfile(UUID.randomUUID().toString(), "UNKNOWN"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Profile retrieval not allowed for role: UNKNOWN");
        }
    }
}
