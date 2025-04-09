package id.ac.ui.cs.advprog.authentication.dto;

import lombok.Data;
import lombok.Generated;

@Generated
@Data
public class ProfileUpdateDto {
    private String fullName;
    private String phoneNumber;
    private String password;
    private String address;
    private String profilePhoto;
    private Integer experience;
}
