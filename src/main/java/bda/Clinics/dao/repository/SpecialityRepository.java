package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    boolean existsByName(String name);

    List<Speciality> findByIsActiveTrue();
}
