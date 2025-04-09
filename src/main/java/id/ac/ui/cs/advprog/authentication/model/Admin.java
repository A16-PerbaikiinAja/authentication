package id.ac.ui.cs.advprog.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name = "admins")
@Generated
@Data
public class Admin {

    public Admin() {
    }

    public Admin(String fullName, String email, String phoneNumber, String password) {
    }
}
