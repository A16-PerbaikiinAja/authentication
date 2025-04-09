package id.ac.ui.cs.advprog.authentication.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testAdminGettersAndSetters() {
        Admin admin = new Admin();
        admin.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        admin.setFullName("Admin Name");
        admin.setEmail("admin@example.com");
        admin.setPhoneNumber("1234567890");
        admin.setPassword("secret");

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), admin.getId());
        assertEquals("Admin Name", admin.getFullName());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("1234567890", admin.getPhoneNumber());
        assertEquals("secret", admin.getPassword());
    }

    @Test
    void testAdminToString() {
        Admin admin = new Admin("Admin Name", "admin@example.com", "1234567890", "secret");
        admin.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        String toString = admin.toString();
        assertTrue(toString.contains("Admin"));
        assertTrue(toString.contains("admin@example.com"));
    }
}
