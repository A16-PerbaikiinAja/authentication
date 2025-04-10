package id.ac.ui.cs.advprog.authentication.service;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;

public interface ProfileService {
    Object updateProfile(ProfileUpdateDto dto, String userId, String role) throws Exception;
    Object getProfile(String userId, String role) throws Exception;
}
