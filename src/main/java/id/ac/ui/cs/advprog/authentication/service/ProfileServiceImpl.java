package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, TechnicianRepository technicianRepository) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
    }

    @Override
    public Object updateProfile(ProfileUpdateDto dto, String email, String role) throws Exception {
        if ("USER".equalsIgnoreCase(role)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));
            if(dto.getFullName() != null) {
                user.setFullName(dto.getFullName());
            }
            if(dto.getPhoneNumber() != null) {
                user.setPhoneNumber(dto.getPhoneNumber());
            }
            if(dto.getAddress() != null) {
                user.setAddress(dto.getAddress());
            }
            if(dto.getProfilePhoto() != null) {
                user.setProfilePhoto(dto.getProfilePhoto());
            }
            if(dto.getPassword() != null) {
                String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
                user.setPassword(hashed);
            }
            return userRepository.save(user);
        } else if ("TECHNICIAN".equalsIgnoreCase(role)) {
            Technician technician = technicianRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("Technician not found"));
            if(dto.getFullName() != null) {
                technician.setFullName(dto.getFullName());
            }
            if(dto.getPhoneNumber() != null) {
                technician.setPhoneNumber(dto.getPhoneNumber());
            }
            if(dto.getAddress() != null) {
                technician.setAddress(dto.getAddress());
            }
            if(dto.getProfilePhoto() != null) {
                technician.setProfilePhoto(dto.getProfilePhoto());
            }
            if(dto.getPassword() != null) {
                String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
                technician.setPassword(hashed);
            }
            if(dto.getExperience() != null) {
                technician.setExperience(dto.getExperience());
            }
            return technicianRepository.save(technician);
        } else {
            throw new Exception("Profile update not allowed for role: " + role);
        }
    }

    @Override
    public Object getProfile(String email, String role) throws Exception {
        if ("USER".equalsIgnoreCase(role)) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));
        } else if ("TECHNICIAN".equalsIgnoreCase(role)) {
            return technicianRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("Technician not found"));
        } else {
            throw new Exception("Profile retrieval not allowed for role: " + role);
        }
    }
}
