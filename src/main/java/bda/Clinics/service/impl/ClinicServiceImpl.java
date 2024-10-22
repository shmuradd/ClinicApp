package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    }

    @Override
    public Clinic updateClinic(Long clinicId, Clinic clinic) {
        return null;
    }
}
