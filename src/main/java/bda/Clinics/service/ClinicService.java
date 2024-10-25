package bda.Clinics.service;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    List<Clinic> getInactiveClinics();

    void updateClinicStatus(Long clinicId, boolean isActive);

    List<Clinic> getAllClinics();
    Clinic getClinicById(Long clinicId);
    Clinic createClinic(Clinic clinic);
    Clinic updateClinic(Long clinicId, Clinic clinic);
    void deleteClinic(Long clinicId);
    void addScheduleToClinic(Long clinicId, Schedule schedule);

}
