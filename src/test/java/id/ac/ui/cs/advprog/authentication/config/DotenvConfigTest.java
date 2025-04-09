package id.ac.ui.cs.advprog.authentication.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DotenvConfigTest {

    @Test
    void testDotenvBean() {
        Dotenv dotenv = new DotenvConfig().dotenv();
        assertNotNull(dotenv, "Dotenv bean should not be null");
    }
}
