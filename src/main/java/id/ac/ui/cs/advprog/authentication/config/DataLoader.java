package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadAdmin(AdminRepository adminRepository) {
        return args -> {
            // Hard-coded credentials for default admin.
            String adminEmail = "admin@example.com";
            // Check if the admin account exists
            if (adminRepository.findByEmail(adminEmail).isEmpty()) {
                String rawPassword = "admin123";
                // Encrypt the password using BCrypt before storing
                String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

                Admin admin = new Admin("Default Admin", adminEmail, "1234567890", hashedPassword);

                adminRepository.save(admin);
                System.out.println("Default admin account created.");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
