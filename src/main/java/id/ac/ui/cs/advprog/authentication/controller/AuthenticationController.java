package id.ac.ui.cs.advprog.authentication.controller;

import jakarta.annotation.security.PermitAll;
import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.model.Technician;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequest authRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (existingMsg, newMsg) -> existingMsg
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            AuthResponse response = authenticationService.login(authRequest);

            ResponseCookie cookie = ResponseCookie.from("token", response.getToken())
                    .httpOnly(true)
                    .path("/")
                    .maxAge(60 * 60 * 24)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        } catch (Exception ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PermitAll
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (existingMsg, newMsg) -> existingMsg
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            User user = authenticationService.registerUser(registrationDto);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/technician")
    public ResponseEntity<?> registerTechnician(
            @Valid @RequestBody TechnicianRegistrationDto registrationDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (existing, newer) -> existing
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Technician technician = authenticationService.registerTechnician(registrationDto);
            return ResponseEntity.ok(technician);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
