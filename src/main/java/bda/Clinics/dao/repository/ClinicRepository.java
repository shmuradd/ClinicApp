package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic,Long> {
}
