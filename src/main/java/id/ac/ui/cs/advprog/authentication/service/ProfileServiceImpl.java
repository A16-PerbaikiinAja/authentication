package id.ac.ui.cs.advprog.authentication.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileResponseDto;
import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository,
                              TechnicianRepository technicianRepository,
                              AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public ProfileResponseDto updateProfile(ProfileUpdateDto dto,
                                            String userId,
                                            String role) throws Exception {
        UUID id = UUID.fromString(userId);

        switch (role.toUpperCase()) {
            case "USER":
                return updateUser(dto, id);
            case "TECHNICIAN":
                return updateTechnician(dto, id);
            default:
                throw new Exception("Profile update not allowed for role: " + role);
        }
    }

    private ProfileResponseDto updateUser(ProfileUpdateDto dto, UUID id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        if (dto.getFullName()    != null) user.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress()     != null) user.setAddress(dto.getAddress());
        if (dto.getProfilePhoto()!= null) user.setProfilePhoto(dto.getProfilePhoto());

        User saved = userRepository.save(user);

        return ProfileResponseDto.builder()
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .address(saved.getAddress())
                .profilePhoto(saved.getProfilePhoto())
                .role("USER")
                .build();
    }

    private ProfileResponseDto updateTechnician(ProfileUpdateDto dto, UUID id) throws Exception {
        Technician tech = technicianRepository.findById(id)
                .orElseThrow(() -> new Exception("Technician not found"));

        if (dto.getFullName()     != null) tech.setFullName(dto.getFullName());
        if (dto.getPhoneNumber()  != null) tech.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress()      != null) tech.setAddress(dto.getAddress());
        if (dto.getProfilePhoto() != null) tech.setProfilePhoto(dto.getProfilePhoto());
        if (dto.getExperience()   != null) tech.setExperience(dto.getExperience());

        Technician saved = technicianRepository.save(tech);

        return ProfileResponseDto.builder()
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .address(saved.getAddress())
                .profilePhoto(saved.getProfilePhoto())
                .experience(saved.getExperience())
                .totalJobsCompleted(saved.getTotalJobsCompleted())
                .totalEarnings(saved.getTotalEarnings())
                .role("TECHNICIAN")
                .build();
    }

    @Override
    public ProfileResponseDto getProfile(String userId, String role) throws Exception {
        UUID id = UUID.fromString(userId);

        switch (role.toUpperCase()) {
            case "USER":
                return getUser(id);
            case "TECHNICIAN":
                return getTechnician(id);
            case "ADMIN":
                return getAdmin(id);
            default:
                throw new Exception("Profile retrieval not allowed for role: " + role);
        }
    }

    private ProfileResponseDto getUser(UUID id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        return ProfileResponseDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePhoto(user.getProfilePhoto())
                .role("USER")
                .build();
    }

    private ProfileResponseDto getTechnician(UUID id) throws Exception {
        Technician tech = technicianRepository.findById(id)
                .orElseThrow(() -> new Exception("Technician not found"));

        return ProfileResponseDto.builder()
                .fullName(tech.getFullName())
                .email(tech.getEmail())
                .phoneNumber(tech.getPhoneNumber())
                .address(tech.getAddress())
                .profilePhoto(tech.getProfilePhoto())
                .experience(tech.getExperience())
                .totalJobsCompleted(tech.getTotalJobsCompleted())
                .totalEarnings(tech.getTotalEarnings())
                .role("TECHNICIAN")
                .build();
    }

    private ProfileResponseDto getAdmin(UUID id) throws Exception {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new Exception("Admin not found"));

        return ProfileResponseDto.builder()
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .phoneNumber(admin.getPhoneNumber())
                .role("ADMIN")
                .build();
    }
}
