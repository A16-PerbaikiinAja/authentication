package id.ac.ui.cs.advprog.authentication.service;

import java.util.Optional;
import java.util.regex.Pattern;

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
        String email       = request.getEmail();
        String rawPassword = request.getPassword();

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        Optional<Technician> techOpt = technicianRepository.findByEmail(email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        String storedHash;
        String role;
        if (adminOpt.isPresent()) {
            storedHash = adminOpt.get().getPassword();
            role       = "ADMIN";
        } else if (techOpt.isPresent()) {
            storedHash = techOpt.get().getPassword();
            role       = "TECHNICIAN";
        } else if (userOpt.isPresent()) {
            storedHash = userOpt.get().getPassword();
            role       = "USER";
        } else {
            throw new Exception("No account found for email: " + email);
        }

        if (!BCrypt.checkpw(rawPassword, storedHash)) {
            throw new Exception("Invalid password for email: " + email);
        }

        String userId;
        if (adminOpt.isPresent()) {
            userId = adminOpt.get().getId().toString();
        } else if (techOpt.isPresent()) {
            userId = techOpt.get().getId().toString();
        } else {
            userId = userOpt.get().getId().toString();
        }

        String token = jwtTokenProvider.generateToken(userId, role);
        return new AuthResponse(token);
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (!Pattern.compile("(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}")
                .matcher(dto.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be ≥8 characters and include a digit, uppercase letter, and special character"
            );
        }

        String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        User user = new User(
                dto.getFullName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                hashed,
                dto.getAddress()
        );
        return userRepository.save(user);
    }

    @Override
    public Technician registerTechnician(TechnicianRegistrationDto dto) {
        if (technicianRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (!Pattern.compile("(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}")
                .matcher(dto.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be ≥8 characters and include a digit, uppercase letter, and special character"
            );
        }

        String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        Technician tech = new Technician(
                dto.getFullName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                hashed,
                dto.getExperience(),
                dto.getAddress(),
                0,
                0.0
        );
        return technicianRepository.save(tech);
    }
}
