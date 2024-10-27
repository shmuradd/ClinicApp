package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.util.location.LocationOperation;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.service.DoctorService;
import bda.Clinics.util.DoctorSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    @Qualifier("put")
    private final ModelMapper modelMapper;
    @Qualifier("patch")
    private  final ModelMapper modelMapperPatch;
    private final LocationOperation locationOperation;
    private final ClinicRepository clinicRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             @Qualifier("put") ModelMapper modelMapper,
                             @Qualifier("patch") ModelMapper modelMapperPatch,
                             LocationOperation locationOperation, ClinicRepository clinicRepository) {
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
        this.modelMapperPatch = modelMapperPatch;
        this.locationOperation = locationOperation;
        this.clinicRepository = clinicRepository;
    }

    @Override
    public List<ResponseDoctorDto> getDoctorsBySpecialty(RequestDoctorDto requestDoctorDto) {

        double radiusInKm = 10.0;  // 10 km radius

        if (requestDoctorDto == null) {
            throw new IllegalArgumentException("Request data cannot be null");
        }
        if (requestDoctorDto.getLocation() == null || requestDoctorDto.getLocation().isEmpty()) {
            requestDoctorDto.setLocation("Baku, Azerbaijan");
        }


        List<ResponseDoctorDto> responseDoctorDtoList = doctorRepository.findAll( Specification
                        .where(DoctorSpecification.hasFullName(requestDoctorDto.getFullName()))
                        .and(DoctorSpecification.hasSpeciality(requestDoctorDto.getSpeciality()))
                        .and(DoctorSpecification.hasClinic(requestDoctorDto.getClinicName())))
                .stream()
                .map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class))
                .collect(Collectors.toList());

        if (responseDoctorDtoList.isEmpty()) {
//            List<ResponseDoctorDto> collect = doctorRepository.findAll().stream().map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class)).collect(Collectors.toList());
//            return locationOperation.doctorSearchForLocationSpecWithinRadius(collect, requestDoctorDto, radiusInKm);
//
            return Collections.emptyList();
        } else {
            return locationOperation.doctorSearchForLocationSpecWithinRadius(responseDoctorDtoList, requestDoctorDto, radiusInKm);
        }

    }


    @Override
    public List<ResponseDoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class)).collect(Collectors.toList());
    }

    public List<Doctor> getInactiveDoctors() {
        return doctorRepository.findByIsActiveFalse();
    }
    public void updateDoctorStatus(Long doctorId, boolean isActive) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        doctor.setIsActive(isActive);
        doctorRepository.save(doctor);
        log.info("Doctor ID: {} status updated to {}", doctorId, isActive);
    }
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Long doctorId, Doctor doctor) {
        Doctor existingDoctor = getDoctorById(doctorId);
        modelMapperPatch.map(existingDoctor,doctor);
        return doctorRepository.save(existingDoctor);
    }

    @Override
    public void deleteDoctor(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }
    @Override
    public void addClinicToDoctor(Long doctorId, Clinic clinic) {
        Doctor doctor = getDoctorById(doctorId);
        doctor.getClinics().add(clinic);
        doctorRepository.save(doctor);
    }

    public void addScheduleToDoctor(Long doctorId, Schedule schedule) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        doctor.getSchedules().add(schedule);
        doctorRepository.save(doctor);
    }
}