package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor,Long> , JpaSpecificationExecutor<Doctor> {

}
