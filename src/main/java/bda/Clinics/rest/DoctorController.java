package bda.Clinics.rest;

import bda.Clinics.dao.model.dto.request.CreateDoctorSpecialityDto;
import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.enums.ReviewStatus;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.service.DoctorService;
import bda.Clinics.service.impl.CloudinaryService;
import bda.Clinics.service.impl.DoctorServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://64.226.99.16:3000",
        "https://topdoc.com.az/",
        "https://topdoc.com.az"}) // Both frontend URLs

public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    private final DoctorServiceImpl doctorServiceImpl;
    private final CloudinaryService cloudinaryService;
    @GetMapping("/doctors/inactive")
    public ResponseEntity<List<Doctor>> getInactiveDoctors() {
        List<Doctor> inactiveDoctors = doctorService.getInactiveDoctors();
        return ResponseEntity.ok(inactiveDoctors);
    }

    @PostMapping("/specification")
    public ResponseEntity<List<ResponseDoctorDto>> findAll(@RequestBody RequestDoctorDto requestDoctorDto,
                                                           @RequestParam(required = false) String sortBy) {
        List<ResponseDoctorDto> doctorsBySpecialty = doctorService.getDoctorsBySpecialty(requestDoctorDto);

        // Filter active doctors with no approved reviews
        List<ResponseDoctorDto> responseDoctorDtoList = doctorsBySpecialty.stream()
                .filter(doctor -> doctor.getIsActive().equals(true))
                // Filter by minimum review count if specified
                .filter(doctor -> requestDoctorDto.getReviewCount() == null ||
                        doctor.getReviews().size() >= requestDoctorDto.getReviewCount())
                // Filter by minimum rating if specified
                .filter(doctor -> requestDoctorDto.getRatingCount() == null ||
                        doctor.getRating() >= requestDoctorDto.getRatingCount())
                .collect(Collectors.toList());

        // Sort based on provided criteria
        if (sortBy == null || sortBy.isEmpty()) {
            responseDoctorDtoList.sort(Comparator.comparingDouble(doctorService::getMinimumClinicDistance));
        } else if ("reviewCount".equalsIgnoreCase(sortBy)) {
            responseDoctorDtoList.sort(Comparator.comparingInt((ResponseDoctorDto doctor) -> doctor.getReviews().size()).reversed());
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            responseDoctorDtoList.sort(Comparator.comparing(ResponseDoctorDto::getRating).reversed());
        }

        return ResponseEntity.ok(responseDoctorDtoList);
    }


    @GetMapping("/all")
    public List<ResponseDoctorDto> findAll() {
        List<ResponseDoctorDto> responseDoctorDtos = doctorService.findAll();



        // Sort doctors: inactive doctors first
        responseDoctorDtos.sort((doctor1, doctor2) -> {
            boolean doctor1Inactive = !doctor1.getIsActive();
            boolean doctor2Inactive = !doctor2.getIsActive();

            // Return doctor1 first if inactive, else doctor2
            return Boolean.compare(doctor1Inactive, doctor2Inactive);
        });
        Collections.reverse(responseDoctorDtos);


        return responseDoctorDtos;
    }

    @PutMapping("/{doctorId}/status")
    public ResponseEntity<Void> updateDoctorStatus(@PathVariable Long doctorId, @RequestParam boolean isActive) {
        doctorService.updateDoctorStatus(doctorId, isActive);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDoctorDto> getDoctorById(@PathVariable Long id) {
        ResponseDoctorDto doctor = doctorService.getDoctorDtoById(id);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Doctor> createDoctor(
            @RequestPart("doctor") RequestDoctorDto doctorDto,
            @RequestPart("photo") MultipartFile photo) throws IOException {

        Doctor createdDoctor = doctorService.createDoctor(doctorDto, photo);
        return ResponseEntity.ok(createdDoctor);
    }




    @PutMapping("/{id}")
    public void updateDoctor(
            @PathVariable Long id,
            @RequestPart("doctor") RequestDoctorDto doctorDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        // Default photoUrl to null or some placeholder if no photo is provided
        String photoUrl = null;

        // Handle the photo upload if provided
        if (photo != null && !photo.isEmpty()) {
            photoUrl = cloudinaryService.uploadImage(photo);
        }

        // Call the service to update the doctor
        doctorService.updateDoctor(id, doctorDto, photoUrl);
    }
    // Endpoint for deleting a clinic from a doctor
    @DeleteMapping("/{doctorId}/clinic/{clinicId}")
    public ResponseEntity<String> deleteClinicFromDoctor(@PathVariable Long doctorId, @PathVariable Long clinicId) {
        try {
            // Call service method to delete clinic
            doctorService.deleteClinicFromDoctor(doctorId, clinicId);
            return ResponseEntity.ok("Clinic removed successfully.");
        } catch (OpenApiResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the clinic.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{doctorId}/clinics")
    public ResponseEntity<Void> addClinicToDoctor(@PathVariable Long doctorId, @RequestBody Clinic clinic) {
        doctorService.addClinicToDoctor(doctorId, clinic);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/specialties")
    public List<String> getSpecialties() {
        return doctorService.getDistinctSpecialities();
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Doctor> toggleDoctorStatus(@PathVariable Long id) {
        Doctor updatedDoctor = doctorService.toggleDoctorStatus(id);
        return ResponseEntity.ok(updatedDoctor);
    }

    @PostMapping("/create-speciality")
    public ResponseEntity<Doctor> createDoctorWithSpeciality(@RequestBody CreateDoctorSpecialityDto doctorSpecialityDto) {
        // Call the service to create a doctor with the specified speciality
        Doctor createdDoctor = doctorService.createDoctorWithSpeciality(doctorSpecialityDto);

        // Return the created doctor
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }
}


