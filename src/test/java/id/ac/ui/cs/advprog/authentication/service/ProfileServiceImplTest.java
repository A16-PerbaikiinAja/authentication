package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.UUID;

class ProfileServiceImplTest {

    private UserRepository userRepository;
    private TechnicianRepository technicianRepository;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        technicianRepository = Mockito.mock(TechnicianRepository.class);
        profileService = new ProfileServiceImpl(userRepository, technicianRepository);
    }

    @Test
    void testUpdateUserProfileSuccess() throws Exception {
        // Setup a dummy user with initial data.
        User user = new User("John Doe", "john@example.com", "+123456789", "oldPassword", "Old Address");
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        user.setId(userId);
        // Stub repository to return the user when findByEmail is called.
        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        // Create DTO with updated fields.
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("John Smith");
        dto.setPhoneNumber("+987654321");
        dto.setAddress("New Address");
        dto.setProfilePhoto("new-user.png");
        dto.setPassword("newPassword");
        // Stub save method to return the updated user.
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updatedUser = (User) profileService.updateProfile(dto, "john@example.com", "USER");
        // Verify that fields are updated.
        assertEquals("John Smith", updatedUser.getFullName());
        assertEquals("+987654321", updatedUser.getPhoneNumber());
        assertEquals("New Address", updatedUser.getAddress());
        assertEquals("new-user.png", updatedUser.getProfilePhoto());
        // Verify that the password is hashed (i.e. not equal to plain "newPassword").
        assertNotEquals("newPassword", updatedUser.getPassword());
    }

    @Test
    void testUpdateTechnicianProfileSuccess() throws Exception {
        // Setup a dummy technician.
        Technician technician = new Technician("Tech One", "tech@example.com", "1112223333", "oldTechPassword", 3, "Old Tech Address", 0, 0.0);
        UUID techId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        technician.setId(techId);
        Mockito.when(technicianRepository.findByEmail("tech@example.com")).thenReturn(Optional.of(technician));
        Mockito.when(technicianRepository.save(Mockito.any(Technician.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName("Tech Updated");
        dto.setPhoneNumber("9998887777");
        dto.setAddress("New Tech Address");
        dto.setProfilePhoto("new-tech.png");
        dto.setPassword("newTechPassword");
        dto.setExperience(5);
        Technician updatedTech = (Technician) profileService.updateProfile(dto, "tech@example.com", "TECHNICIAN");
        assertEquals("Tech Updated", updatedTech.getFullName());
        assertEquals("9998887777", updatedTech.getPhoneNumber());
        assertEquals("New Tech Address", updatedTech.getAddress());
        assertEquals("new-tech.png", updatedTech.getProfilePhoto());
        assertEquals(5, updatedTech.getExperience());
        assertNotEquals("newTechPassword", updatedTech.getPassword());
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        Mockito.when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        ProfileUpdateDto dto = new ProfileUpdateDto();
        Exception exception = assertThrows(Exception.class, () ->
                profileService.updateProfile(dto, "nonexistent@example.com", "USER"));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testUpdateTechnicianProfile_TechnicianNotFound() {
        Mockito.when(technicianRepository.findByEmail("nonexistenttech@example.com")).thenReturn(Optional.empty());
        ProfileUpdateDto dto = new ProfileUpdateDto();
        Exception exception = assertThrows(Exception.class, () ->
                profileService.updateProfile(dto, "nonexistenttech@example.com", "TECHNICIAN"));
        assertTrue(exception.getMessage().contains("Technician not found"));
    }
}
