package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    private AdminRepository adminRepo;
    private TechnicianRepository techRepo;
    private UserRepository userRepo;
    private JwtTokenProvider jwtProv;
    private AuthenticationServiceImpl svc;

    @BeforeEach
    void init() {
        adminRepo = mock(AdminRepository.class);
        techRepo  = mock(TechnicianRepository.class);
        userRepo  = mock(UserRepository.class);
        jwtProv   = mock(JwtTokenProvider.class);
        svc = new AuthenticationServiceImpl(adminRepo, techRepo, userRepo, jwtProv);
    }

    @Nested
    class LoginTests {

        @Test
        void success_admin() throws Exception {
            String email = "admin@x.com";
            String rawPwd = "Admin1!";
            String id = UUID.randomUUID().toString();
            String hashed = BCrypt.hashpw(rawPwd, BCrypt.gensalt());

            Admin a = new Admin("A", email, "p", hashed);
            a.setId(UUID.fromString(id));
            when(adminRepo.findByEmail(email)).thenReturn(Optional.of(a));
            when(jwtProv.generateToken(id, "ADMIN")).thenReturn("tok-ADMIN");

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword(rawPwd);

            var resp = svc.login(req);
            assertEquals("tok-ADMIN", resp.getToken());
        }

        @Test
        void success_technician() throws Exception {
            String email = "tech@x.com";
            String rawPwd = "Tech1!";
            String id = UUID.randomUUID().toString();
            String hashed = BCrypt.hashpw(rawPwd, BCrypt.gensalt());

            when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
            Technician t = new Technician("T", email, "p", hashed, 0, "addr", 0, 0.0);
            t.setId(UUID.fromString(id));
            when(techRepo.findByEmail(email)).thenReturn(Optional.of(t));
            when(jwtProv.generateToken(id, "TECHNICIAN")).thenReturn("tok-TECHNICIAN");

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword(rawPwd);

            var resp = svc.login(req);
            assertEquals("tok-TECHNICIAN", resp.getToken());
        }

        @Test
        void success_user() throws Exception {
            String email = "user@x.com";
            String rawPwd = "User1!";
            String id = UUID.randomUUID().toString();
            String hashed = BCrypt.hashpw(rawPwd, BCrypt.gensalt());

            when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
            when(techRepo.findByEmail(email)).thenReturn(Optional.empty());
            User u = new User("U", email, "p", hashed, "addr");
            u.setId(UUID.fromString(id));
            when(userRepo.findByEmail(email)).thenReturn(Optional.of(u));
            when(jwtProv.generateToken(id, "USER")).thenReturn("tok-USER");

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword(rawPwd);

            var resp = svc.login(req);
            assertEquals("tok-USER", resp.getToken());
        }

        @Test
        void noAccount() throws Exception {
            String email = "x@x.com";
            when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
            when(techRepo.findByEmail(email)).thenReturn(Optional.empty());
            when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword("pwd");

            Exception ex = assertThrows(Exception.class, () -> svc.login(req));
            assertTrue(ex.getMessage().contains("No account found for email: " + email));
        }

        @Test
        void badPassword() throws Exception {
            String email = "a@x.com";
            String rawPwd = "wrong";
            String hashed = BCrypt.hashpw("other", BCrypt.gensalt());

            Admin a = new Admin("A", email, "p", hashed);
            when(adminRepo.findByEmail(email)).thenReturn(Optional.of(a));

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword(rawPwd);

            Exception ex = assertThrows(Exception.class, () -> svc.login(req));
            assertTrue(ex.getMessage().contains("Invalid password for email: " + email));
        }

        @Test
        void jwtError() throws Exception {
            String email = "u@x.com";
            String rawPwd = "Pwd1!";
            String id = UUID.randomUUID().toString();
            String hashed = BCrypt.hashpw(rawPwd, BCrypt.gensalt());

            when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
            when(techRepo.findByEmail(email)).thenReturn(Optional.empty());
            User u = new User("U", email, "p", hashed, "");
            u.setId(UUID.fromString(id));
            when(userRepo.findByEmail(email)).thenReturn(Optional.of(u));
            when(jwtProv.generateToken(any(), any())).thenThrow(new RuntimeException("JWT fail"));

            AuthRequest req = new AuthRequest();
            req.setEmail(email);
            req.setPassword(rawPwd);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> svc.login(req));
            assertEquals("JWT fail", ex.getMessage());
        }
    }

    @Nested
    class RegisterTests {

        @Test
        void userSuccess() {
            var dto = new UserRegistrationDto();
            dto.setFullName("X");
            dto.setEmail("x@x.com");
            dto.setPhoneNumber("+1");
            dto.setPassword("Password1!");
            dto.setAddress("addr");

            when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
            when(userRepo.save(any(User.class))).thenAnswer(inv -> {
                var u = inv.getArgument(0, User.class);
                u.setId(UUID.randomUUID());
                return u;
            });

            User result = svc.registerUser(dto);
            assertNotNull(result.getId());
            assertEquals("x@x.com", result.getEmail());
        }

        @Test
        void userEmailConflict() {
            var dto = new UserRegistrationDto();
            dto.setFullName("X");
            dto.setEmail("x@x.com");
            dto.setPhoneNumber("+1");
            dto.setPassword("Password1!");
            dto.setAddress("addr");

            when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> svc.registerUser(dto));
            assertTrue(ex.getMessage().contains("Email is already in use"));
        }

        @Test
        void userPasswordFail() {
            var dto = new UserRegistrationDto();
            dto.setFullName("X");
            dto.setEmail("x2@x.com");
            dto.setPhoneNumber("+1");
            dto.setPassword("simple"); // too weak
            dto.setAddress("addr");

            when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> svc.registerUser(dto));
            assertTrue(ex.getMessage().contains("Password must be"));
        }

        @Test
        void techEmailConflict() {
            var dto = new TechnicianRegistrationDto();
            dto.setFullName("T");
            dto.setEmail("t@x.com");
            dto.setPhoneNumber("+1");
            dto.setPassword("Pass1!");
            dto.setExperience(0);
            dto.setAddress("addr");

            when(techRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Technician()));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> svc.registerTechnician(dto));
            assertTrue(ex.getMessage().contains("Email is already in use"));
        }
    }
}
