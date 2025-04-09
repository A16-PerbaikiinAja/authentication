package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

class DataLoaderTest {

    @Test
    void testLoadAdmin_CreatesAdminIfNoneExists() throws Exception {
        AdminRepository adminRepository = mock(AdminRepository.class);
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        CommandLineRunner runner = new DataLoader().loadAdmin(adminRepository);
        runner.run(new String[0]);
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void testLoadAdmin_DoesNotCreateAdminIfExists() throws Exception {
        AdminRepository adminRepository = mock(AdminRepository.class);
        Admin existingAdmin = new Admin("Existing Admin", "admin@example.com", "1234567890", "hashedPassword");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(existingAdmin));
        CommandLineRunner runner = new DataLoader().loadAdmin(adminRepository);
        runner.run(new String[0]);
        verify(adminRepository, never()).save(any(Admin.class));
    }
}
