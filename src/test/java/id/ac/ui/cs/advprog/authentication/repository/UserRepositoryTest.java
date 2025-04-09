package id.ac.ui.cs.advprog.authentication.repository;

import id.ac.ui.cs.advprog.authentication.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        User user = new User("User Name", "user@example.com", "1112223333", "hashedPassword", "Address");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("user@example.com");
        assertTrue(found.isPresent());
        assertEquals("User Name", found.get().getFullName());
    }
}
