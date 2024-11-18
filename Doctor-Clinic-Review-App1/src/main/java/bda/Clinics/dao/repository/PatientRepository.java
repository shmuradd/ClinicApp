package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long> {
}
