package bda.Clinics.rest;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bda.Clinics.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://64.226.99.16:3000"}) // Both frontend URLs
public class ClinicController {
    private final ClinicService clinicService;
    @GetMapping("/clinics/inactive")
    public ResponseEntity<List<ResponseClinicDto>> getInactiveClinics() {
        List<ResponseClinicDto> inactiveClinics = clinicService.getInactiveClinics();
        return ResponseEntity.ok(inactiveClinics);
    }
    @PutMapping("/{clinicId}/status")
    public ResponseEntity<Void> updateClinicStatus(@PathVariable Long clinicId, @RequestParam boolean isActive) {
        clinicService.updateClinicStatus(clinicId, isActive);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<ResponseClinicDto>> getAllClinics() {
        List<ResponseClinicDto> clinics = clinicService.getAllClinics();
        return ResponseEntity.ok(clinics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseClinicDto> getClinicById(@PathVariable Long id) {
        ResponseClinicDto clinic = clinicService.getClinicById(id);
        return ResponseEntity.ok(clinic);
    }

    @PostMapping
    public ResponseEntity<ResponseClinicDto> createClinic(@RequestBody Clinic clinic) {
        ResponseClinicDto createdClinic = clinicService.createClinic(clinic);
        return ResponseEntity.ok(createdClinic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseClinicDto> updateClinic(@PathVariable Long id, @RequestBody Clinic clinic) {
        ResponseClinicDto updatedClinic = clinicService.updateClinic(id, clinic);
        return ResponseEntity.ok(updatedClinic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build();
    }

}