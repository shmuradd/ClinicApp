package bda.Clinics.service.impl;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import lombok.extern.slf4j.Slf4j;
import bda.Clinics.dao.model.entity.Schedule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static ch.qos.logback.core.joran.spi.ConsoleTarget.findByName;

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
        try {
            // Check if the clinic's location is empty or null
            if (clinic.getLocation() == null || clinic.getLocation().isEmpty()) {
                // Set Baku's default Google Maps link if location is empty
                clinic.setLocation("https://www.google.com/maps/place/Bak%C3%BC/@40.394737,49.6901489,40414m/data=!3m2!1e3!4b1!4m6!3m5!1s0x40307d6bd6211cf9:0x343f6b5e7ae56c6b!8m2!3d40.4092617!4d49.8670924!16zL20vMDFnZjU?entry=ttu&g_ep=EgoyMDI0MTAyOS4wIKXMDSoASAFQAw%3D%3D");
            } else {
                // Encode the provided location into a Google Maps link
                String encodedLocation = URLEncoder.encode(clinic.getLocation(), StandardCharsets.UTF_8.toString());
                String googleMapsLink = "https://www.google.com/maps/search/?api=1&query=" + encodedLocation;
                clinic.setLocation(googleMapsLink);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: handle any encoding exceptions here, or log the error
        }
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


    public Clinic saveClinic(Clinic clinic) {
        return clinicRepository.save(clinic);
    }
}