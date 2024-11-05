package bda.Clinics.rest;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.service.ClinicService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://64.226.99.16:3000",
        "https://topdoc.com.az/",
        "https://topdoc.com.az"}) // Both frontend URLs
public class ClinicController {
    private final ClinicService clinicService;
    @GetMapping("/clinics/inactive")
    public ResponseEntity<List<Clinic>> getInactiveClinics() {
        List<Clinic> inactiveClinics = clinicService.getInactiveClinics();
        return ResponseEntity.ok(inactiveClinics);
    }
    @PutMapping("/{clinicId}/status")
    public ResponseEntity<Void> updateClinicStatus(@PathVariable Long clinicId, @RequestParam boolean isActive) {
        clinicService.updateClinicStatus(clinicId, isActive);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicService.getAllClinics();
        return ResponseEntity.ok(clinics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable Long id) {
        Clinic clinic = clinicService.getClinicById(id);
        return ResponseEntity.ok(clinic);
    }

    @PostMapping
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        Clinic createdClinic = clinicService.createClinic(clinic);
        return ResponseEntity.ok(createdClinic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clinic> updateClinic(@PathVariable Long id, @RequestBody Clinic clinic) {
        Clinic updatedClinic = clinicService.updateClinic(id, clinic);
        return ResponseEntity.ok(updatedClinic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build();
    }

}