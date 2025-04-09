package id.ac.ui.cs.advprog.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.TechnicianRegistrationDto;
import id.ac.ui.cs.advprog.authentication.dto.UserRegistrationDto;
import id.ac.ui.cs.advprog.authentication.service.AuthenticationService;

public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
    }

    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    }

    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
    }

    public ResponseEntity<?> registerTechnician(@RequestBody TechnicianRegistrationDto registrationDto) {
    }
}
