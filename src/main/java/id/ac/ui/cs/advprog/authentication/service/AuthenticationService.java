package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;

public interface AuthenticationService {

    AuthResponse login(AuthRequest request) throws Exception;

    void registerUser(UserRegistrationDto registrationDto);

    void registerTechnician(TechnicianRegistrationDto registrationDto);

    void changePassword(String userId, String oldPassword, String newPassword);
}
