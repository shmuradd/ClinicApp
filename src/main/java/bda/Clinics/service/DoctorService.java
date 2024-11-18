package bda.Clinics.service;

import bda.Clinics.dao.model.dto.request.RequestScheduleDto;
import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.entity.Schedule;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<ResponseDoctorDto> getDoctorsBySpecialty(RequestDoctorDto requestDoctorDto);

    List<ResponseDoctorDto> findAll();
    List<Doctor> getInactiveDoctors();
    void updateDoctorStatus(Long doctorId, boolean isActive);
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long doctorId);
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(Long doctorId, Doctor doctor);
    void deleteDoctor(Long doctorId);
    ResponseDoctorDto getDoctorDtoById(Long doctorId);
    void addClinicToDoctor(Long doctorId, Clinic clinic);
    void addScheduleToDoctor(Long doctorId, Schedule schedule);
    List<String> getDistinctSpecialities();
    double getMinimumClinicDistance(ResponseDoctorDto doctor);
    void sortDoctors(List<ResponseDoctorDto> doctors, String sortBy);
    Doctor toggleDoctorStatus(Long doctorId);

    Optional<Doctor> findDoctorByFullNameAndSpeciality(String fullName, String speciality);
    Doctor createDoctor(RequestDoctorDto doctorDto, MultipartFile photo) throws IOException;

    public void addScheduleToDoctorAndClinic(Long doctorId, Long clinicId, RequestScheduleDto requestScheduleDto);
}
