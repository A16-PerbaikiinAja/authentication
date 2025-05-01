package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.model.Admin;
import id.ac.ui.cs.advprog.authentication.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    private static final String DEFAULT_NAME     = "Default Admin";
    private static final String DEFAULT_EMAIL    = "admin@example.com";
    private static final String DEFAULT_PHONE    = "1234567890";
    private static final String DEFAULT_PASSWORD = "rawPassword";

    @Test
    void testLoadAdmin_CreatesAdminIfNoneExists() throws Exception {
        AdminRepository adminRepository = mock(AdminRepository.class);
        when(adminRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.empty());

        CommandLineRunner runner = new DataLoader()
                .loadAdmin(
                        adminRepository,
                        DEFAULT_NAME,
                        DEFAULT_EMAIL,
                        DEFAULT_PHONE,
                        DEFAULT_PASSWORD
                );
        runner.run(new String[0]);

        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void testLoadAdmin_DoesNotCreateAdminIfExists() throws Exception {
        AdminRepository adminRepository = mock(AdminRepository.class);
        Admin existingAdmin = new Admin(
                DEFAULT_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE,
                "alreadyHashed"
        );
        when(adminRepository.findByEmail(DEFAULT_EMAIL))
                .thenReturn(Optional.of(existingAdmin));

        CommandLineRunner runner = new DataLoader()
                .loadAdmin(
                        adminRepository,
                        DEFAULT_NAME,
                        DEFAULT_EMAIL,
                        DEFAULT_PHONE,
                        DEFAULT_PASSWORD
                );
        runner.run(new String[0]);

        verify(adminRepository, never()).save(any(Admin.class));
    }
}
