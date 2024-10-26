package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> , JpaSpecificationExecutor<Doctor> {
    @EntityGraph(value = "doctor.clinics")
    List<Doctor> findAll();
    Optional<Doctor> findByFullName(String fullName);
    @Query("SELECT MAX(d.doctorId) FROM Doctor d")
    Long findMaxDoctorId();
    Doctor getDoctorByDoctorId(Long doctorId);
    List<Doctor> findByIsActiveFalse();

}
