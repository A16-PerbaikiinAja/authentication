package id.ac.ui.cs.advprog.authentication.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void testFindByEmail() {
    }
}
