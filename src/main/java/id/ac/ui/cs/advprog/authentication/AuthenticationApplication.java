package id.ac.ui.cs.advprog.authentication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationApplication {

    public static void main(String[] args) {

        String activeProfile = System.getProperty("spring.profiles.active");
        if (!"test".equals(activeProfile)) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL"));
            System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME"));
            System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD"));

            System.setProperty("spring.jpa.properties.hibernate.dialect", dotenv.get("HIBERNATE_DIALECT"));
            System.setProperty("spring.jpa.hibernate.ddl-auto", dotenv.get("JPA_DDL_AUTO"));
            System.setProperty("spring.jpa.show-sql", dotenv.get("SHOW_SQL"));
        }

        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
