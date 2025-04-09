package id.ac.ui.cs.advprog.authentication.repository;

import id.ac.ui.cs.advprog.authentication.model.Technician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TechnicianRepositoryTest {

    @Autowired
    private TechnicianRepository technicianRepository;

    @Test
    void testFindByEmail() {
        Technician tech = new Technician("Tech Name", "tech@example.com", "0987654321", "hashedPassword", 5, "Address", 0, 0.0);
        technicianRepository.save(tech);

        Optional<Technician> found = technicianRepository.findByEmail("tech@example.com");
        assertTrue(found.isPresent());
        assertEquals("Tech Name", found.get().getFullName());
    }
}
