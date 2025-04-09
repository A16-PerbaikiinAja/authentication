package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;

public interface AuthenticationService {

    AuthResponse login(AuthRequest request) throws Exception;

    User registerUser(UserRegistrationDto registrationDto);

    Technician registerTechnician(TechnicianRegistrationDto registrationDto);
}
