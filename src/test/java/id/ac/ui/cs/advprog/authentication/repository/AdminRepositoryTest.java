package id.ac.ui.cs.advprog.authentication.repository;

import id.ac.ui.cs.advprog.authentication.model.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void testFindByEmail() {
        Admin admin = new Admin("Admin Name", "admin@example.com", "1234567890", "hashedPassword");
        adminRepository.save(admin);

        Optional<Admin> found = adminRepository.findByEmail("admin@example.com");
        assertTrue(found.isPresent());
        assertEquals("Admin Name", found.get().getFullName());
    }
}
