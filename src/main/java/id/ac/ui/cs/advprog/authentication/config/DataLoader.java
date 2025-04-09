package id.ac.ui.cs.advprog.authentication.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadAdmin(AdminRepository adminRepository) {
    }
}
