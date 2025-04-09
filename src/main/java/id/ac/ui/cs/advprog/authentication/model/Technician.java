package id.ac.ui.cs.advprog.authentication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Entity
@Table(name = "technicians")
@Generated
@Data
public class Technician {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "experience")
    private Integer experience;  // optional

    @Column(name = "address")
    private String address;

    @Column(name = "total_jobs_completed", nullable = false)
    private int totalJobsCompleted = 0;

    @Column(name = "total_earnings", nullable = false)
    private double totalEarnings = 0.0;

    @Column(name = "profile_photo")
    private String profilePhoto = "default-technician.png";

    public Technician() {
    }

    public Technician(String fullName, String email, String phoneNumber, String password,
                      Integer experience, String address, int totalJobsCompleted, double totalEarnings) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.experience = experience;
        this.address = address;
        this.totalJobsCompleted = totalJobsCompleted;
        this.totalEarnings = totalEarnings;
        this.profilePhoto = "default-technician.png";
    }
}
