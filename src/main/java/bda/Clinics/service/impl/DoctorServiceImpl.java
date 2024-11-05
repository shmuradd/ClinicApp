package bda.Clinics.service.impl;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.entity.Schedule;
import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.service.ClinicService;
import bda.Clinics.util.location.LocationOperation;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.service.DoctorService;
import bda.Clinics.util.DoctorSpecification;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    private final ClinicService clinicService;
    private final CloudinaryService cloudinaryService;
    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             @Qualifier("put") ModelMapper modelMapper,
                             @Qualifier("patch") ModelMapper modelMapperPatch,
                             LocationOperation locationOperation, ClinicRepository clinicRepository, ClinicService clinicService, CloudinaryService cloudinaryService) {
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
        this.modelMapperPatch = modelMapperPatch;
        this.locationOperation = locationOperation;
        this.clinicRepository = clinicRepository;
        this.clinicService = clinicService;
        this.cloudinaryService = cloudinaryService;
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

        Specification<Doctor> spec = Specification.where(null);

        if (requestDoctorDto.getFullName() != null && !requestDoctorDto.getFullName().isEmpty()) {
            spec = spec.and(DoctorSpecification.hasFullName(requestDoctorDto.getFullName()));
        }
        if (requestDoctorDto.getSpeciality() != null && !requestDoctorDto.getSpeciality().isEmpty()) {
            spec = spec.and(DoctorSpecification.hasSpeciality(requestDoctorDto.getSpeciality()));
        }
        if (requestDoctorDto.getClinicName() != null && !requestDoctorDto.getClinicName().isEmpty()) {
            spec = spec.and(DoctorSpecification.hasClinic(requestDoctorDto.getClinicName()));
        }

        List<ResponseDoctorDto> responseDoctorDtoList = doctorRepository.findAll(spec)
                .stream()
                .map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class))
                .collect(Collectors.toList());

        // If clinicName was specified and no doctors were found, return an empty list
        if (requestDoctorDto.getClinicName() != null && !requestDoctorDto.getClinicName().isEmpty() && responseDoctorDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // If no results found but location-based fallback is needed
        if (responseDoctorDtoList.isEmpty()) {
            List<ResponseDoctorDto> allDoctors = doctorRepository.findAll()
                    .stream()
                    .map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class))
                    .collect(Collectors.toList());
            return locationOperation.doctorSearchForLocationSpecWithinRadius(allDoctors, requestDoctorDto, radiusInKm);
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
//    @Override
//    public Doctor createDoctor(RequestDoctorDto doctorDto, MultipartFile photo) throws IOException {
//        String photoPath = savePhoto(photo);
//
//        Doctor doctor = Doctor.builder()
//                .fullName(doctorDto.getFullName())
//                .speciality(doctorDto.getSpeciality())
//                .photoUrl(photoPath)
//                .isActive(false) // Default to true for demonstration
//                .build();
//
//        return doctorRepository.save(doctor);
//    }

    private String savePhoto(MultipartFile photo) throws IOException {
        if (photo.isEmpty()) {
            return null;
        }

        // Create the uploads directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create a unique filename to avoid conflicts
        String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath);

        // Return the relative path for database storage
        return "/uploads/" + fileName;
    }

    @Override
    public Optional<Doctor> findDoctorByFullNameAndSpeciality(String fullName, String speciality) {
        return doctorRepository.findByFullNameAndSpeciality(fullName, speciality);
    }

    @Override
    public Doctor updateDoctor(Long doctorId, Doctor doctor) {
        Doctor existingDoctor = getDoctorById(doctorId);
        modelMapperPatch.map(doctor,existingDoctor);
        return doctorRepository.save(existingDoctor);
    }

    @Override
    public void deleteDoctor(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }

    @Override
    public ResponseDoctorDto getDoctorDtoById(Long doctorId) {
        // Retrieve the doctor by ID, or throw an exception if not found
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        // Map the Doctor entity to a ResponseDoctorDto
        return modelMapper.map(doctor, ResponseDoctorDto.class);
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
    @Override
    public List<String> getDistinctSpecialities() {
        return doctorRepository.findDistinctSpecialities();
    }
    @Override
    public double getMinimumClinicDistance(ResponseDoctorDto doctor) {
        return doctor.getClinics().stream()
                .mapToDouble(RequestClinicDto::getDistance)  // Assuming ClinicDto has a getDistance() method
                .min()  // Get the minimum distance
                .orElse(Double.MAX_VALUE);  // Return a large number if no clinics are available
    }
    @Override
    public void sortDoctors(List<ResponseDoctorDto> doctors, String sortBy) {
        if ("reviewCount".equalsIgnoreCase(sortBy)) {
            doctors.sort(Comparator.comparingInt(doctor -> doctor.getReviews().size()));
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            doctors.sort(Comparator.comparing(ResponseDoctorDto::getRating).reversed());
        }
    }
    @Override
    public Doctor toggleDoctorStatus(Long doctorId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            doctor.setIsActive(!doctor.getIsActive()); // Toggle status
            return doctorRepository.save(doctor); // Save the updated doctor
        } else {
            throw new RuntimeException("Doctor not found with ID: " + doctorId);
        }
    }
    @Override
    public Doctor createDoctor(RequestDoctorDto doctorDto, MultipartFile photo) throws IOException {
        // Validate the photo input
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }

        // Upload the image to Cloudinary and get the URL
        String photoUrl;
        try {
            photoUrl = cloudinaryService.uploadImage(photo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }

        // Create and set up the Doctor entity
        Doctor doctor = new Doctor();
        doctor.setFullName(doctorDto.getFullName());
        doctor.setSpeciality(doctorDto.getSpeciality());
        doctor.setIsActive(false);  // Set to true if the doctor should be active
        doctor.setPhotoUrl(photoUrl);  // Save the file URL to the database

        // Check if the clinic exists; if not, create a new one
        Clinic clinic = clinicRepository.findByClinicName(doctorDto.getClinicName())
                .orElseGet(() -> {
                    Clinic newClinic = new Clinic();
                    newClinic.setClinicName(doctorDto.getClinicName());
                    // Set other properties if needed
                    return clinicService.saveClinic(newClinic); // Save the new clinic
                });

        // Add the clinic to the doctor
        doctor.addClinics(clinic); // Use the new addClinics method

        // Save and return the Doctor object
        return doctorRepository.save(doctor);
    }


}


