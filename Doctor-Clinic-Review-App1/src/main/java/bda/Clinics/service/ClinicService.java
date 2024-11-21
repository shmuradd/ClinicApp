package bda.Clinics.service;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.dao.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    List<ResponseClinicDto> getInactiveClinics();

    void updateClinicStatus(Long clinicId, boolean isActive);

    List<ResponseClinicDto> getAllClinics();
    ResponseClinicDto getClinicById(Long clinicId);
    ResponseClinicDto createClinic(Clinic clinic);
    ResponseClinicDto updateClinic(Long clinicId, Clinic clinic);
    void deleteClinic(Long clinicId);
    void addScheduleToClinic(Long clinicId, Schedule schedule);

}
