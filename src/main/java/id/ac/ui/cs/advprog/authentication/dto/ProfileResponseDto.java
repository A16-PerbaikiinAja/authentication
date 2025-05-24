package id.ac.ui.cs.advprog.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Generated;

@Generated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDto {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePhoto;
    private Integer experience;
    private Integer totalJobsCompleted;
    private Double totalEarnings;
    private String role;
}
