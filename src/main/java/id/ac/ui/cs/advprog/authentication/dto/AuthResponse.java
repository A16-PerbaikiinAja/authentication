package id.ac.ui.cs.advprog.authentication.dto;

import lombok.Data;
import lombok.Generated;

@Generated
@Data
public class AuthResponse {
    
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
