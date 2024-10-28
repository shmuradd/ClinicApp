package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    }

    @Override
    public Clinic updateClinic(Long clinicId, Clinic clinic) {
        Clinic existingClinic = getClinicById(clinicId);
        modelMapper.map(clinic,existingClinic);
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


}