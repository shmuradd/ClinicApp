package bda.Clinics.rest;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinic")
@RequiredArgsConstructor
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
}
