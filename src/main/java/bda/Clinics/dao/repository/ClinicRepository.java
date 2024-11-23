package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic,Long> {

    Optional<Clinic> findByClinicName(String clinicName);

    List<Clinic> findByIsActiveFalse();

    Optional<Clinic> findByClinicNameAndDoctors_DoctorId(String clinicName, Long doctorId);
}
