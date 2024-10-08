package bda.Clinics.service;

import bda.Clinics.dao.model.Clinic;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    List<Clinic> getAllClinics();

    Optional<Clinic> getClinicById(Long clinicId);

    Clinic saveClinic(Clinic clinic);

    void deleteClinic(Long clinicId);

    Clinic updateClinic(Long clinicId, Clinic clinic);

}
