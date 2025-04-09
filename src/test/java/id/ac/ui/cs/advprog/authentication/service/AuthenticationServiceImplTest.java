package id.ac.ui.cs.advprog.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void testLogin_AdminSuccess() throws Exception {
    }

    @Test
    void testLogin_TechnicianSuccess() throws Exception {
    }

    @Test
    void testLogin_UserSuccess() throws Exception {
    }

    @Test
    void testLogin_InvalidPassword() {
    }

    @Test
    void testLogin_NoAccountFound() {
    }

    @Test
    void testRegisterUser() {
    }

    @Test
    void testRegisterTechnician() {
    }
}
