package id.ac.ui.cs.advprog.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
    }

    public ResponseEntity<?> getProfile(Authentication authentication) {
    }

    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDto dto, Authentication authentication) {
    }
}
