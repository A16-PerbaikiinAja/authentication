package id.ac.ui.cs.advprog.authentication.service;

import java.util.Optional;
import java.util.UUID;
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

    private static final Pattern PASSWORD_POLICY =
            Pattern.compile("(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}");

    private final AdminRepository adminRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationServiceImpl(
            AdminRepository adminRepository,
            TechnicianRepository technicianRepository,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.adminRepository = adminRepository;
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail();
        String rawPassword = request.getPassword();

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        Optional<Technician> techOpt = technicianRepository.findByEmail(email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        String storedHash;
        String role;

        if (adminOpt.isPresent()) {
            storedHash = adminOpt.get().getPassword();
            role = "ADMIN";
        } else if (techOpt.isPresent()) {
            storedHash = techOpt.get().getPassword();
            role = "TECHNICIAN";
        } else if (userOpt.isPresent()) {
            storedHash = userOpt.get().getPassword();
            role = "USER";
        } else {
            throw new IllegalArgumentException(
                    "No account found for email: " + email
            );
        }

        if (!BCrypt.checkpw(rawPassword, storedHash)) {
            throw new IllegalArgumentException(
                    "Invalid password for email: " + email
            );
        }

        String userId = adminOpt.map(a -> a.getId().toString())
                .orElseGet(() -> techOpt.map(t -> t.getId().toString())
                        .orElseGet(() -> userOpt.get().getId().toString()));

        String token = jwtTokenProvider.generateToken(userId, role);
        return new AuthResponse(token);
    }

    @Override
    public void registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        validatePassword(dto.getPassword());

        String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        User user = new User(
                dto.getFullName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                hashed,
                dto.getAddress()
        );

        userRepository.save(user);
    }

    @Override
    public void registerTechnician(TechnicianRegistrationDto dto) {
        if (technicianRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        validatePassword(dto.getPassword());

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

        technicianRepository.save(tech);
    }

    @Override
    public void changePassword(
            String userId,
            String oldPassword,
            String newPassword
    ) {
        UUID id = UUID.fromString(userId);

        Optional<Admin> adminOpt = adminRepository.findById(id);
        Optional<Technician> techOpt = technicianRepository.findById(id);
        Optional<User> userOpt = userRepository.findById(id);

        if (adminOpt.isPresent()) {
            changePasswordForEntity(adminOpt.get(), oldPassword, newPassword);
            adminRepository.save(adminOpt.get());
        } else if (techOpt.isPresent()) {
            changePasswordForEntity(techOpt.get(), oldPassword, newPassword);
            technicianRepository.save(techOpt.get());
        } else if (userOpt.isPresent()) {
            changePasswordForEntity(userOpt.get(), oldPassword, newPassword);
            userRepository.save(userOpt.get());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    private void changePasswordForEntity(
            Object entity,
            String oldPassword,
            String newPassword
    ) {
        String currentHash = getCurrentHash(entity);

        if (!BCrypt.checkpw(oldPassword, currentHash)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(newPassword);

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        setNewHash(entity, newHash);
    }

    private void validatePassword(String password) {
        if (!PASSWORD_POLICY.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must be ≥8 characters and include a digit, uppercase letter, and special character"
            );
        }
    }

    private String getCurrentHash(Object entity) {
        if (entity instanceof Admin) {
            return ((Admin) entity).getPassword();
        } else if (entity instanceof Technician) {
            return ((Technician) entity).getPassword();
        } else if (entity instanceof User) {
            return ((User) entity).getPassword();
        } else {
            throw new IllegalArgumentException("Unsupported account type");
        }
    }

    private void setNewHash(Object entity, String newHash) {
        if (entity instanceof Admin) {
            ((Admin) entity).setPassword(newHash);
        } else if (entity instanceof Technician) {
            ((Technician) entity).setPassword(newHash);
        } else {
            ((User) entity).setPassword(newHash);
        }
    }
}
