package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadAdmin(
            AdminRepository adminRepository,
            @Value("${DEFAULT_ADMIN_NAME:Admin}") String adminName,
            @Value("${DEFAULT_ADMIN_EMAIL:admin@example.com}") String adminEmail,
            @Value("${DEFAULT_ADMIN_PHONE:0000000000}") String adminPhone,
            @Value("${DEFAULT_ADMIN_PASSWORD:ChangeMe123!}") String adminRawPassword
    ) {
        return args -> {
            if (adminRepository.findByEmail(adminEmail).isEmpty()) {
                String hashedPassword = BCrypt.hashpw(adminRawPassword, BCrypt.gensalt());
                Admin admin = new Admin(adminName, adminEmail, adminPhone, hashedPassword);
                adminRepository.save(admin);
                System.out.println("Default admin account created from environment variables.");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
