package bda.Clinics.service;

import bda.Clinics.dao.model.Clinic;
<<<<<<< HEAD
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
=======
import bda.Clinics.dao.model.Schedule;
>>>>>>> ca36d09666c6b4914e78fdbce3c2429b31964fe1

import java.util.List;
import java.util.Optional;

public interface ClinicService {
<<<<<<< HEAD
    public List<ResponseClinicDto> getAllClinics();

    Optional<Clinic> getClinicById(Long clinicId);

    Clinic saveClinic(Clinic clinic);

    void deleteClinic(Long clinicId);

=======
    List<Clinic> getInactiveClinics();

    void updateClinicStatus(Long clinicId, boolean isActive);

    List<Clinic> getAllClinics();
    Clinic getClinicById(Long clinicId);
    Clinic createClinic(Clinic clinic);
>>>>>>> ca36d09666c6b4914e78fdbce3c2429b31964fe1
    Clinic updateClinic(Long clinicId, Clinic clinic);
    void deleteClinic(Long clinicId);
    void addScheduleToClinic(Long clinicId, Schedule schedule);

}
