package id.ac.ui.cs.advprog.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Generated;

@Generated
@Data
public class AuthRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Must be a well-formed email address")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
