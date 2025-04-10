package id.ac.ui.cs.advprog.authentication.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.authentication.model.Technician;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {
    Optional<Technician> findByEmail(String email);
}
