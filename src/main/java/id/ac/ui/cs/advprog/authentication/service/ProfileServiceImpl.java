package id.ac.ui.cs.advprog.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.repository.TechnicianRepository;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, TechnicianRepository technicianRepository) {
    }

    @Override
    public Object updateProfile(ProfileUpdateDto dto, String email, String role) throws Exception {
    }

    @Override
    public Object getProfile(String email, String role) throws Exception {
    }
}
