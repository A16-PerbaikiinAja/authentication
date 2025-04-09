package id.ac.ui.cs.advprog.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.authentication.security.JwtTokenProvider;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AdminRepository adminRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationServiceImpl(AdminRepository adminRepository,
                                     TechnicianRepository technicianRepository,
                                     UserRepository userRepository,
                                     JwtTokenProvider jwtTokenProvider) {
    }

    @Override
    public AuthResponse login(AuthRequest request) throws Exception {
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
    }

    @Override
    public Technician registerTechnician(TechnicianRegistrationDto registrationDto) {
}
