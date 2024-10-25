package bda.Clinics.service;

import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface DoctorService {
    List<ResponseDoctorDto> getDoctorsBySpecialty(RequestDoctorDto requestDoctorDto);

    List<ResponseDoctorDto> findAll();
    List<Doctor> getInactiveDoctors();
    void updateDoctorStatus(Long doctorId, boolean isActive);

}
