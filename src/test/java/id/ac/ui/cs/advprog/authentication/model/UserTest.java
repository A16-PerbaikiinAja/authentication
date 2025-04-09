package id.ac.ui.cs.advprog.authentication.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        user.setFullName("User Name");
        user.setEmail("user@example.com");
        user.setPhoneNumber("1112223333");
        user.setPassword("userSecret");
        user.setAddress("User address");
        user.setProfilePhoto("user-photo.png");

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000005"), user.getId());
        assertEquals("User Name", user.getFullName());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("1112223333", user.getPhoneNumber());
        assertEquals("userSecret", user.getPassword());
        assertEquals("User address", user.getAddress());
        assertEquals("user-photo.png", user.getProfilePhoto());
    }

    @Test
    void testUserToString() {
        User user = new User("User Name", "user@example.com", "1112223333", "userSecret", "User address");
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000006"));
        String toString = user.toString();
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("user@example.com"));
    }
}
