package id.ac.ui.cs.advprog.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;

class ProfileServiceImplTest {

    private UserRepository userRepository;
    private TechnicianRepository technicianRepository;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testUpdateUserProfileSuccess() throws Exception {
    }

    @Test
    void testUpdateTechnicianProfileSuccess() throws Exception {
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
    }

    @Test
    void testUpdateTechnicianProfile_TechnicianNotFound() {
    }
}
