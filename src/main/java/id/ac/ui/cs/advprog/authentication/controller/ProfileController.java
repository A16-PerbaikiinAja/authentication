package id.ac.ui.cs.advprog.authentication.controller;

import id.ac.ui.cs.advprog.authentication.dto.ProfileUpdateDto;
import id.ac.ui.cs.advprog.authentication.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = extractEmail(authentication);
        String role = extractRole(authentication);
        try {
            Object profile = profileService.getProfile(email, role);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDto dto, Authentication authentication) {
        String email = extractEmail(authentication);
        String role = extractRole(authentication);
        try {
            Object updatedProfile = profileService.updateProfile(dto, email, role);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to extract email from the principal.
    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        } else if (principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        }
    }

    // Helper method to extract role from the authorities.
    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }
}
