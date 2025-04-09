package id.ac.ui.cs.advprog.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name = "technicians")
@Generated
@Data
public class Technician {

    public Technician() {
    }

    public Technician(String fullName, String email, String phoneNumber, String password,
                      Integer experience, String address, int totalJobsCompleted, double totalEarnings) {
    }
}
