package id.ac.ui.cs.advprog.authentication.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.ChangePasswordDto;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(mapValidationErrors(bindingResult));
        }
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PermitAll
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegistrationDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(mapValidationErrors(bindingResult));
        }
        try {
            authService.registerUser(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/technician")
    public ResponseEntity<?> registerTechnician(
            @Valid @RequestBody TechnicianRegistrationDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(mapValidationErrors(bindingResult));
        }
        try {
            authService.registerTechnician(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordDto dto,
            BindingResult bindingResult,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(mapValidationErrors(bindingResult));
        }
        try {
            authService.changePassword(
                    principal.getName(),
                    dto.getOldPassword(),
                    dto.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, String> mapValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, newer) -> existing
                ));
    }
}
