package id.ac.ui.cs.advprog.authentication.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TechnicianTest {

    @Test
    void testTechnicianGettersAndSetters() {
        Technician tech = new Technician();
        tech.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        tech.setFullName("Tech Name");
        tech.setEmail("tech@example.com");
        tech.setPhoneNumber("0987654321");
        tech.setPassword("techSecret");
        tech.setExperience(5);
        tech.setAddress("Some address");
        tech.setTotalJobsCompleted(10);
        tech.setTotalEarnings(1000.0);
        tech.setProfilePhoto("tech-photo.png");

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), tech.getId());
        assertEquals("Tech Name", tech.getFullName());
        assertEquals("tech@example.com", tech.getEmail());
        assertEquals("0987654321", tech.getPhoneNumber());
        assertEquals("techSecret", tech.getPassword());
        assertEquals(5, tech.getExperience());
        assertEquals("Some address", tech.getAddress());
        assertEquals(10, tech.getTotalJobsCompleted());
        assertEquals(1000.0, tech.getTotalEarnings());
        assertEquals("tech-photo.png", tech.getProfilePhoto());
    }

    @Test
    void testTechnicianToString() {
        Technician tech = new Technician("Tech Name", "tech@example.com", "0987654321", "techSecret", 5, "Some address", 10, 1000.0);
        tech.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        String toString = tech.toString();
        assertTrue(toString.contains("Tech"));
        assertTrue(toString.contains("tech@example.com"));
    }
}
