package id.ac.ui.cs.advprog.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.authentication.dto.ProfileResponseDto;
import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private ProfileServiceImpl service;

    private UUID userId;
    private UUID techId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        techId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    @Test
    void updateProfileUser_allFields() throws Exception {
        // Arrange
        User user = new User();
        user.setId(userId);
        user.setFullName("Old");
        user.setEmail("u@example.com");
        user.setPhoneNumber("111");
        user.setAddress("Addr1");
        user.setProfilePhoto("old.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("New");
        dto.setPhoneNumber("222");
        dto.setAddress("Addr2");
        dto.setProfilePhoto("new.png");

        // Act
        ProfileResponseDto resp = service.updateProfile(dto, userId.toString(), "USER");

        // Assert
        assertEquals("New", resp.getFullName());
        assertEquals("u@example.com", resp.getEmail());
        assertEquals("222", resp.getPhoneNumber());
        assertEquals("Addr2", resp.getAddress());
        assertEquals("new.png", resp.getProfilePhoto());
        assertNull(resp.getExperience());
        assertNull(resp.getTotalEarnings());
        assertNull(resp.getTotalJobsCompleted());
    }

    @Test
    void updateProfileUser_partial() throws Exception {
        // Arrange
        User user = new User();
        user.setId(userId);
        user.setFullName("Old");
        user.setEmail("u@example.com");
        user.setPhoneNumber("111");
        user.setAddress("Addr1");
        user.setProfilePhoto("old.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setPhoneNumber("333");

        // Act
        ProfileResponseDto resp = service.updateProfile(dto, userId.toString(), "USER");

        // Assert
        assertEquals("Old", resp.getFullName());
        assertEquals("u@example.com", resp.getEmail());
        assertEquals("333", resp.getPhoneNumber());
        assertEquals("Addr1", resp.getAddress());
        assertEquals("old.png", resp.getProfilePhoto());
    }

    @Test
    void updateProfileTechnician_allFields() throws Exception {
        // Arrange
        Technician tech = new Technician();
        tech.setId(techId);
        tech.setFullName("TOld");
        tech.setEmail("t@example.com");
        tech.setPhoneNumber("444");
        tech.setAddress("TAddr1");
        tech.setProfilePhoto("told.png");
        tech.setExperience(1);
        tech.setTotalEarnings(100.0);
        tech.setTotalJobsCompleted(2);

        when(technicianRepository.findById(techId)).thenReturn(Optional.of(tech));
        when(technicianRepository.save(any(Technician.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("TNew");
        dto.setExperience(5);

        // Act
        ProfileResponseDto resp = service.updateProfile(dto, techId.toString(), "TECHNICIAN");

        // Assert
        assertEquals("TNew", resp.getFullName());
        assertEquals("t@example.com", resp.getEmail());
        assertEquals("444", resp.getPhoneNumber());
        assertEquals("TAddr1", resp.getAddress());
        assertEquals("told.png", resp.getProfilePhoto());
        assertEquals(Integer.valueOf(5), resp.getExperience());
        assertEquals(Double.valueOf(100.0), resp.getTotalEarnings());
        assertEquals(Integer.valueOf(2), resp.getTotalJobsCompleted());
    }

    @Test
    void updateProfileTechnician_partial() throws Exception {
        // Arrange
        Technician tech = new Technician();
        tech.setId(techId);
        tech.setFullName("TOld");
        tech.setEmail("t@example.com");
        tech.setPhoneNumber("444");
        tech.setAddress("TAddr1");
        tech.setProfilePhoto("told.png");
        tech.setExperience(1);
        tech.setTotalEarnings(100.0);
        tech.setTotalJobsCompleted(2);

        when(technicianRepository.findById(techId)).thenReturn(Optional.of(tech));
        when(technicianRepository.save(any(Technician.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setProfilePhoto("tnew.png");

        // Act
        ProfileResponseDto resp = service.updateProfile(dto, techId.toString(), "TECHNICIAN");

        // Assert
        assertEquals("TOld", resp.getFullName());
        assertEquals("t@example.com", resp.getEmail());
        assertEquals("444", resp.getPhoneNumber());
        assertEquals("TAddr1", resp.getAddress());
        assertEquals("tnew.png", resp.getProfilePhoto());
        assertEquals(Integer.valueOf(1), resp.getExperience());
        assertEquals(Double.valueOf(100.0), resp.getTotalEarnings());
        assertEquals(Integer.valueOf(2), resp.getTotalJobsCompleted());
    }

    @Test
    void updateProfileAdmin_throwsException() {
        assertThrows(
                Exception.class,
                () -> service.updateProfile(new ProfileUpdateDto(), adminId.toString(), "ADMIN")
        );
    }

    @Test
    void getProfileUser() throws Exception {
        // Arrange
        User user = new User();
        user.setId(userId);
        user.setFullName("UF");
        user.setEmail("u2@example.com");
        user.setPhoneNumber("666");
        user.setAddress("UA");
        user.setProfilePhoto("up.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ProfileResponseDto resp = service.getProfile(userId.toString(), "USER");

        // Assert
        assertEquals("UF", resp.getFullName());
        assertEquals("u2@example.com", resp.getEmail());
        assertEquals("666", resp.getPhoneNumber());
        assertEquals("UA", resp.getAddress());
        assertEquals("up.png", resp.getProfilePhoto());
    }

    @Test
    void getProfileTechnician() throws Exception {
        // Arrange
        Technician tech = new Technician();
        tech.setId(techId);
        tech.setFullName("TF");
        tech.setEmail("t2@example.com");
        tech.setPhoneNumber("777");
        tech.setAddress("TA");
        tech.setProfilePhoto("tp.png");
        tech.setExperience(3);
        tech.setTotalEarnings(200.0);
        tech.setTotalJobsCompleted(4);

        when(technicianRepository.findById(techId)).thenReturn(Optional.of(tech));

        // Act
        ProfileResponseDto resp = service.getProfile(techId.toString(), "TECHNICIAN");

        // Assert
        assertEquals("TF", resp.getFullName());
        assertEquals("t2@example.com", resp.getEmail());
        assertEquals("777", resp.getPhoneNumber());
        assertEquals("TA", resp.getAddress());
        assertEquals("tp.png", resp.getProfilePhoto());
        assertEquals(Integer.valueOf(3), resp.getExperience());
        assertEquals(Double.valueOf(200.0), resp.getTotalEarnings());
        assertEquals(Integer.valueOf(4), resp.getTotalJobsCompleted());
    }

    @Test
    void getProfileAdmin() throws Exception {
        // Arrange
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setFullName("AF");
        admin.setEmail("a2@example.com");
        admin.setPhoneNumber("888");

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // Act
        ProfileResponseDto resp = service.getProfile(adminId.toString(), "ADMIN");

        // Assert
        assertEquals("AF", resp.getFullName());
        assertEquals("a2@example.com", resp.getEmail());
        assertEquals("888", resp.getPhoneNumber());
        assertNull(resp.getAddress());
        assertNull(resp.getProfilePhoto());
    }

    @Test
    void updateProfileInvalidRole_throwsException() {
        assertThrows(
                Exception.class,
                () -> service.updateProfile(new ProfileUpdateDto(), UUID.randomUUID().toString(), "XYZ")
        );
    }

    @Test
    void getProfileInvalidRole_throwsException() {
        assertThrows(
                Exception.class,
                () -> service.getProfile(UUID.randomUUID().toString(), "NONE")
        );
    }

    @Test
    void updateProfileUserNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> service.updateProfile(new ProfileUpdateDto(), userId.toString(), "USER")
        );
    }

    @Test
    void getProfileUserNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> service.getProfile(userId.toString(), "USER")
        );
    }
}
