package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
<<<<<<< HEAD
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
=======
import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
>>>>>>> ca36d09666c6b4914e78fdbce3c2429b31964fe1
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
<<<<<<< HEAD
import java.util.stream.Collectors;

@Service
@Slf4j

public class ClinicServiceImpl implements ClinicService {
    @Autowired
    private ClinicRepository clinicRepository;

    @Override
    public List<ResponseClinicDto> getAllClinics() {
        // Retrieve all clinics from the database
        List<Clinic> clinics = clinicRepository.findAll();

        // Map each Clinic to ResponseClinicDto
        return clinics.stream()
                .map(clinic -> new ResponseClinicDto(clinic.getClinicName(), clinic.getLocation(), clinic.getContactDetails(), clinic.getCity())) // Adjust the mapping based on your entity structure
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Clinic> getClinicById(Long clinicId) {
        return Optional.empty();
    }

    @Override
    public Clinic saveClinic(Clinic clinic) {
        return null;
    }

    @Override
    public void deleteClinic(Long clinicId) {

=======

@Service
@Slf4j
public class ClinicServiceImpl implements ClinicService {

    private  final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper;

    public ClinicServiceImpl(ClinicRepository clinicRepository,@Qualifier("patch") ModelMapper modelMapper) {
        this.clinicRepository = clinicRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Clinic> getInactiveClinics() {
        return clinicRepository.findByIsActiveFalse();
    }
    @Override
    public void updateClinicStatus(Long clinicId, boolean isActive) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with ID: " + clinicId));

        clinic.setIsActive(isActive);
        clinicRepository.save(clinic);
        log.info("Clinic ID: {} status updated to {}", clinicId, isActive);
    }

    @Override
    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    @Override
    public Clinic getClinicById(Long clinicId) {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with ID: " + clinicId));
    }

    @Override
    public Clinic createClinic(Clinic clinic) {
        return clinicRepository.save(clinic);
>>>>>>> ca36d09666c6b4914e78fdbce3c2429b31964fe1
    }

    @Override
    public Clinic updateClinic(Long clinicId, Clinic clinic) {
<<<<<<< HEAD
        return null;
    }
=======
        Clinic existingClinic = getClinicById(clinicId);
        modelMapper.map(existingClinic,clinic);
        return clinicRepository.save(existingClinic);
    }

    @Override
    public void deleteClinic(Long clinicId) {
        clinicRepository.deleteById(clinicId);
    }
    public void addScheduleToClinic(Long clinicId, Schedule schedule) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with ID: " + clinicId));
        clinic.getSchedules().add(schedule);
        clinicRepository.save(clinic);
    }


>>>>>>> ca36d09666c6b4914e78fdbce3c2429b31964fe1
}
