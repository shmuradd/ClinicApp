package bda.Clinics.rest;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.enums.ReviewStatus;
import bda.Clinics.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/doctors/inactive")
    public ResponseEntity<List<Doctor>> getInactiveDoctors() {
        List<Doctor> inactiveDoctors = doctorService.getInactiveDoctors();
        return ResponseEntity.ok(inactiveDoctors);
    }

    @GetMapping("/specification")
    public ResponseEntity<List<ResponseDoctorDto>> findAll(@RequestBody RequestDoctorDto requestDoctorDto) {
        List<ResponseDoctorDto> doctorsBySpecialty = doctorService.getDoctorsBySpecialty(requestDoctorDto);
        List<ResponseDoctorDto> responseDoctorDtoList = doctorsBySpecialty.stream().filter(doctor -> doctor.getIsActive().equals(true))
                .filter(doctor -> doctor.getReviews().stream().anyMatch(review -> review.getStatus().equals(ReviewStatus.APPROVED)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDoctorDtoList);
    }

    @GetMapping("/all")
    public List<ResponseDoctorDto> findAll() {
        List<ResponseDoctorDto> responseDoctorDtos = doctorService.findAll();
        List<ResponseDoctorDto> responseDoctorDtoList = responseDoctorDtos.stream().filter(doctor -> doctor.getIsActive().equals(true))
                .filter(doctor -> doctor.getReviews().stream().anyMatch(review -> review.getStatus() == ReviewStatus.APPROVED))
                .collect(Collectors.toList());
        return responseDoctorDtoList;
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
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.ok(createdDoctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(updatedDoctor);
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
}


