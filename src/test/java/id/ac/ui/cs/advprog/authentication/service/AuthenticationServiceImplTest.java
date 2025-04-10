package id.ac.ui.cs.advprog.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;

class AuthenticationServiceImplTest {

    private AdminRepository adminRepository;
    private TechnicianRepository technicianRepository;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationServiceImpl authService;

    @BeforeEach
    void setUp() {
        adminRepository = mock(AdminRepository.class);
        technicianRepository = mock(TechnicianRepository.class);
        userRepository = mock(UserRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authService = new AuthenticationServiceImpl(adminRepository, technicianRepository, userRepository, jwtTokenProvider);
    }

    @Test
    void testLogin_AdminSuccess() throws Exception {
        String email = "admin@example.com";
        String rawPassword = "admin123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        Admin admin = new Admin("Default Admin", email, "1234567890", hashedPassword);
        UUID adminId = UUID.randomUUID();
        admin.setId(adminId);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(jwtTokenProvider.generateToken(adminId.toString(), "ADMIN")).thenReturn("dummy-admin-token");

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("dummy-admin-token", response.getToken());
    }

    @Test
    void testLogin_TechnicianSuccess() throws Exception {
        String email = "tech@example.com";
        String rawPassword = "techpass";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        Technician tech = new Technician("Tech Name", email, "9876543210", hashedPassword, 5, "Address", 0, 0.0);
        UUID techId = UUID.randomUUID();
        tech.setId(techId);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.of(tech));
        when(jwtTokenProvider.generateToken(techId.toString(), "TECHNICIAN")).thenReturn("dummy-tech-token");

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("dummy-tech-token", response.getToken());
    }

    @Test
    void testLogin_UserSuccess() throws Exception {
        String email = "user@example.com";
        String rawPassword = "userpass";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User user = new User("User Name", email, "1112223333", hashedPassword, "User Address");
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(userId.toString(), "USER")).thenReturn("dummy-user-token");

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("dummy-user-token", response.getToken());
    }

    @Test
    void testLogin_InvalidPassword() {
        String email = "admin@example.com";
        String rawPassword = "admin123";
        String hashedPassword = BCrypt.hashpw("differentPassword", BCrypt.gensalt());
        Admin admin = new Admin("Default Admin", email, "1234567890", hashedPassword);
        admin.setId(UUID.randomUUID());

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        Exception exception = assertThrows(Exception.class, () -> authService.login(request));
        assertTrue(exception.getMessage().contains("Invalid password"));
    }

    @Test
    void testLogin_NoAccountFound() {
        String email = "nonexistent@example.com";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword("anyPassword");

        Exception exception = assertThrows(Exception.class, () -> authService.login(request));
        assertTrue(exception.getMessage().contains("No account found"));
    }

    @Test
    void testRegisterUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("+123456789");
        dto.setPassword("password");
        dto.setAddress("123 Main St");

        User savedUser = new User(dto.getFullName(), dto.getEmail(), dto.getPhoneNumber(),
                BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()), dto.getAddress());
        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.registerUser(dto);
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testRegisterTechnician() {
        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setFullName("Tech One");
        dto.setEmail("tech@example.com");
        dto.setPhoneNumber("1112223333");
        dto.setPassword("techpass");
        dto.setExperience(3);
        dto.setAddress("Tech Address");

        Technician technician = new Technician(dto.getFullName(), dto.getEmail(), dto.getPhoneNumber(),
                BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()), dto.getExperience(), dto.getAddress(), 0, 0.0);
        technician.setId(UUID.randomUUID());

        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        Technician result = authService.registerTechnician(dto);
        assertNotNull(result);
        assertEquals("Tech One", result.getFullName());
    }
}
