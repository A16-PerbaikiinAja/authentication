package id.ac.ui.cs.advprog.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthenticationServiceImpl service;

    private final String rawPassword = "Secret1!";
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    @Test
    void loginSucceedsForAdmin() throws Exception {
        // Arrange
        String email = "admin@example.com";
        Admin admin = new Admin();
        admin.setId(UUID.randomUUID());
        admin.setEmail(email);
        admin.setPassword(hashedPassword);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtTokenProvider.generateToken(admin.getId().toString(), "ADMIN"))
                .thenReturn("admintoken");

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        // Act
        AuthResponse resp = service.login(request);

        // Assert
        assertEquals("admintoken", resp.getToken());
        verify(jwtTokenProvider).generateToken(admin.getId().toString(), "ADMIN");
    }

    @Test
    void loginFailsWhenNoAccount() {
        // Arrange
        String email = "nouser@example.com";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        // Act & Assert
        Exception ex = assertThrows(Exception.class, () -> service.login(request));
        assertTrue(ex.getMessage().contains("No account found"));
    }

    @Test
    void loginFailsWithWrongPassword() {
        // Arrange
        String email = "tech@example.com";
        Technician tech = new Technician();
        tech.setId(UUID.randomUUID());
        tech.setEmail(email);
        tech.setPassword(hashedPassword);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.of(tech));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword("WrongPass1!");

        // Act & Assert
        Exception ex = assertThrows(Exception.class, () -> service.login(request));
        assertTrue(ex.getMessage().contains("Invalid password"));
    }

    @Test
    void registerUserSucceeds() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("Valid1!A");
        dto.setFullName("New User");
        dto.setPhoneNumber("123456789");
        dto.setAddress("123 Main St");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // Act
        service.registerUser(dto);

        // Assert
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals(dto.getEmail()) &&
                        u.getFullName().equals(dto.getFullName()) &&
                        BCrypt.checkpw(dto.getPassword(), u.getPassword())
        ));
    }

    @Test
    void registerUserFailsOnDuplicateEmail() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("dup@example.com");
        dto.setPassword("Valid1!");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new User()));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registerUser(dto)
        );
        assertTrue(ex.getMessage().contains("Email is already in use"));
    }

    @Test
    void registerUserFailsOnWeakPassword() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("user@example.com");
        dto.setPassword("weak");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registerUser(dto)
        );
        assertTrue(ex.getMessage().contains("Password must be"));
    }

    @Test
    void registerTechnicianSucceeds() {
        // Arrange
        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setEmail("tech@example.com");
        dto.setPassword("Valid1!A");
        dto.setFullName("Tech Person");
        dto.setPhoneNumber("987654321");
        dto.setAddress("456 Tech Rd");
        dto.setExperience(5);

        when(technicianRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // Act
        service.registerTechnician(dto);

        // Assert
        verify(technicianRepository).save(argThat(t ->
                t.getEmail().equals(dto.getEmail()) &&
                        t.getFullName().equals(dto.getFullName()) &&
                        t.getExperience().equals(dto.getExperience()) &&
                        BCrypt.checkpw(dto.getPassword(), t.getPassword())
        ));
    }

    @Test
    void registerTechnicianFailsOnDuplicateEmail() {
        // Arrange
        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setEmail("duptech@example.com");
        dto.setPassword("Valid1!");

        when(technicianRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new Technician()));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registerTechnician(dto)
        );
        assertTrue(ex.getMessage().contains("Email is already in use"));
    }

    @Test
    void registerTechnicianFailsOnWeakPassword() {
        // Arrange
        TechnicianRegistrationDto dto = new TechnicianRegistrationDto();
        dto.setEmail("tech2@example.com");
        dto.setPassword("weak");

        when(technicianRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registerTechnician(dto)
        );
        assertTrue(ex.getMessage().contains("Password must be"));
    }

    @Test
    void changePasswordSucceedsForUser() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(UUID.fromString(userId));
        user.setPassword(hashedPassword);

        when(adminRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(technicianRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        String newPwd = "NewPass1!";

        // Act
        service.changePassword(userId, rawPassword, newPwd);

        // Assert
        verify(userRepository).save(user);
        assertTrue(BCrypt.checkpw(newPwd, user.getPassword()));
    }

    @Test
    void changePasswordFailsWhenUserNotFound() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UUID id = UUID.fromString(userId);

        when(adminRepository.findById(id)).thenReturn(Optional.empty());
        when(technicianRepository.findById(id)).thenReturn(Optional.empty());
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(userId, rawPassword, "Another1!")
        );
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void changePasswordFailsOnWrongOldPassword() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(UUID.fromString(userId));
        user.setPassword(hashedPassword);

        when(adminRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(technicianRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(userId, "WrongOld1!", "Another1!")
        );
        assertTrue(ex.getMessage().contains("Old password is incorrect"));
    }

    @Test
    void changePasswordFailsOnWeakNewPassword() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(UUID.fromString(userId));
        user.setPassword(hashedPassword);

        when(adminRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(technicianRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(userId, rawPassword, "weak")
        );
        assertTrue(ex.getMessage().contains("Password must be"));
    }
}
