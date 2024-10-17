package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic,Long> {

    Optional<Clinic> findByClinicName(String clinicName);
    @Query("SELECT MAX(c.clinicId) FROM Clinic c")
    Long findMaxClinicId();
}
