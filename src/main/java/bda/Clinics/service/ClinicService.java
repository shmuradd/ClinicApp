package bda.Clinics.service;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Schedule;

import java.util.List;

public interface ClinicService {
    List<Clinic> getInactiveClinics();

    void updateClinicStatus(Long clinicId, boolean isActive);

    List<Clinic> getAllClinics();
    Clinic getClinicById(Long clinicId);
    Clinic createClinic(Clinic clinic);
    Clinic updateClinic(Long clinicId, Clinic clinic);
    void deleteClinic(Long clinicId);
    void addScheduleToClinic(Long clinicId, Schedule schedule);
    public Clinic saveClinic(Clinic clinic);

}
