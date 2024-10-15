package bda.Clinics.service;

import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;

import java.util.List;

public interface DoctorService {
    List<ResponseDoctorDto> getDoctorsBySpecialty(RequestDoctorDto requestDoctorDto);

    List<ResponseDoctorDto> findAll();
    public ResponseDoctorDto getDoctorById(Long doctorId);
}
