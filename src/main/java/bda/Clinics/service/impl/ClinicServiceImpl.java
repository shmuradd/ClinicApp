package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClinicServiceImpl implements ClinicService {

    private  final ClinicRepository clinicRepository;

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
        return null;
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
