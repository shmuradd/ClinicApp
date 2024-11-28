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

    public ClinicServiceImpl(ClinicRepository clinicRepository,@Qualifier("put") ModelMapper modelMapper) {
        this.clinicRepository = clinicRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ResponseClinicDto> getInactiveClinics() {
        return clinicRepository.findByIsActiveFalse().stream().map(clinic -> modelMapper.map(clinic,ResponseClinicDto.class)).collect(Collectors.toList());
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
    public List<ResponseClinicDto> getAllClinics() {
        return clinicRepository.findAll().stream().map(clinic -> modelMapper.map(clinic,ResponseClinicDto.class)).collect(Collectors.toList());
    }

    @Override
    public ResponseClinicDto getClinicById(Long clinicId) {
        Clinic clinic= clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with ID: " + clinicId));
        return modelMapper.map(clinic,ResponseClinicDto.class);
    }

    @Override
    public ResponseClinicDto createClinic(Clinic clinic) {
        Clinic newClinic=clinicRepository.save(clinic);
        return modelMapper.map(newClinic,ResponseClinicDto.class);
    }

    @Override
    public ResponseClinicDto updateClinic(Long clinicId, Clinic clinic) {
        Clinic existingClinic = clinicRepository.findById(clinicId).orElseThrow(() -> new RuntimeException("Clinic not found with ID: " + clinicId));
        clinicRepository.save(existingClinic);
        modelMapper.map(existingClinic,clinic);
        return modelMapper.map(existingClinic,ResponseClinicDto.class);

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