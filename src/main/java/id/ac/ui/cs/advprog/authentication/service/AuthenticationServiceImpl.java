package id.ac.ui.cs.advprog.authentication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

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
        this.adminRepository = adminRepository;
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse login(AuthRequest request) throws Exception {
        String email = request.getEmail();
        String rawPassword = request.getPassword();
        String role = null;
        String storedHashedPassword = null;
        String userId = null;

        // Check Admins first
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            storedHashedPassword = admin.getPassword();
            role = "ADMIN";
            userId = admin.getId().toString();
        } else {
            // Then check Technicians
            Optional<Technician> techOpt = technicianRepository.findByEmail(email);
            if (techOpt.isPresent()) {
                Technician tech = techOpt.get();
                storedHashedPassword = tech.getPassword();
                role = "TECHNICIAN";
                userId = tech.getId().toString();
            } else {
                // Finally check Users
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    storedHashedPassword = user.getPassword();
                    role = "USER";
                    userId = user.getId().toString();
                }
            }
        }

        if (storedHashedPassword == null) {
            throw new Exception("No account found for email: " + email);
        }

        if (!BCrypt.checkpw(rawPassword, storedHashedPassword)) {
            throw new Exception("Invalid password for email: " + email);
        }

        String token = jwtTokenProvider.generateToken(userId, role);
        return new AuthResponse(token);
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        String hashedPassword = BCrypt.hashpw(registrationDto.getPassword(), BCrypt.gensalt());
        User user = new User(
                registrationDto.getFullName(),
                registrationDto.getEmail(),
                registrationDto.getPhoneNumber(),
                hashedPassword,
                registrationDto.getAddress()
        );
        return userRepository.save(user);
    }

    @Override
    public Technician registerTechnician(TechnicianRegistrationDto registrationDto) {
        // Hash the password.
        String hashedPassword = BCrypt.hashpw(registrationDto.getPassword(), BCrypt.gensalt());
        // Create new technician with a dummy profile photo.
        Technician technician = new Technician(
                registrationDto.getFullName(),
                registrationDto.getEmail(),
                registrationDto.getPhoneNumber(),
                hashedPassword,
                registrationDto.getExperience(),
                registrationDto.getAddress(),
                0,
                0.0
        );
        return technicianRepository.save(technician);
    }
}
